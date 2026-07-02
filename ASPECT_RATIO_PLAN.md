# Aspect-Ratio Strategy & Spawn-Pop-In Fix — Implementation Plan

Status: planned, not yet implemented. All paths relative to this repo. Line numbers refer to the current working tree (uncommitted aspect-ratio rework included). No level JSON files are edited.

## Context

The recent (uncommitted) aspect-ratio rework fixed distortion by locking the world height (12.5 m / 800 px) and deriving the width from the device aspect: `Configuration.updateGameViewport()` sets `GAME_WORLD_WIDTH = 12.5 * aspect` (meters) and `GAME_PIXEL_WIDTH = 800 * aspect` (pixels); design reference stays 1280×800 px = 20×12.5 m (64 px/m). GameScreen, MenuScreen, and the static parallax layer were already adapted. Three problems remain:

1. **Pop-in**: JSON-defined powerups (chilis) spawn at the raw JSON `x` — all 30 level files hardcode `"x":25` (2× `"x":24`), i.e. 5 m past the *old* 20 m edge. On 16:9 the world is ~22.2 m, on 21:10 ~26.25 m wide, so chilis materialize *inside* the view. Enemies/obstacles/random chilis already spawn at `GAME_WORLD_WIDTH + 5f` and are correct. Verified at `core/src/com/xsheetgames/genericElements/Level.java:83` vs `:89`.
2. **Menu background**: new 16:9 asset `android/assets/menu/images/menuScreen2.jpg` (1280×720) must replace `menuScreen.jpg` (1280×800), cover mode, cropping on the **right or bottom** (anchor left/top).
3. **Remaining distortion**: 10 screens plus the loading screen still stretch a fixed 1280×800 ortho onto the window. Also: scrolling parallax layers only guarantee ~20 m of coverage (gap on wide screens), and three hardcoded world-edge values (two bosses, boss energy meter) break on non-16:10.

Strategy for all aspect ratios (4:3, 16:9, 16:10, 21:10): full-bleed **cover** backgrounds (center/center default) drawn in screen pixels + **FitViewport(1280×800)** for UI sprites/touch — exactly the pattern MenuScreen already implements inline. World physics untouched (Box2D fixed timestep unchanged); level experience preserved (same speeds/ctimes; spawn offsets re-anchored to the actual right edge, keeping the designed 4 m/5 m lead-in).

## Verified facts (from exploration)

- Touch: `InputManager.java:168-183` passes **raw screen pixels** to `AbstractScreen.screenTouched/AfterTouched/WhileTouch`; each screen converts with the stretch formula `x*TARGET_WIDTH/screenW, TARGET_HEIGHT - y*TARGET_HEIGHT/screenH` (ChooseLevelpackScreen:182, SettingsScreen:378, ChooseLevelScreen:343 — has an int-division bug, InputScreen:171, CreditsScreen:155, DeadScreen:213, LevelDoneScreen:364). PreBossScreen/StartPackScreen/EndPackScreen ignore coordinates. MenuScreen already uses `uiViewport.unproject` (MenuScreen:293-299).
- Despawn safety: Chili sets `this.bodyType = KinematicBody` (Chili.java:29), so `GameObjectCollection.removeOutlaws()` (:103-122) routes it to the branch **without a right-edge check** — spawning at/past `GAME_WORLD_WIDTH+5` can never be instantly despawned.
- ParallaxLayer bug confirmed (`ParallaxLayer.java:50-68`): the next tile spawns only when the lead tile crosses `x <= 0`; worst-case covered right edge = one tile width = exactly 20 m (all layer textures are 1280×800). Any world > 20 m gets a periodic gap in the scrolling (middle/front) layers.
- Hardcoded world-edge values: `BossBat.java:233` (`pos.x >= 16.6f`), `BossSnake.java:499` (`pos.x >= 13.5f`), `BossEnergyMeter.java:21/27/54/60` (920f/936f = 1280−360/−344 → overflows the ~1066 px 4:3 HUD).
- All other `VIEWPORT_WIDTH` usages are pixel↔meter sprite-size ratios and must stay as-is (GameObject.java:137/152/207/307, Draco.java:164/167/175/390/393/401, BossSnake.java:106/174/347, InputButton.java:32/34/85/87, AtlasAnimation.java:26/63, ParallaxLayer.java:85). Boundary walls already use GAME_WORLD_WIDTH (BoundaryCollection.java:14-16); GameScreen HUD pause cluster / distance track fit at 4:3.
- Backgrounds of the fixed-ortho screens (all 1280×800): `menu/images/credits_back.jpg` (ChooseLevelpack/ChooseLevel/Settings/Input/Credits), `game/images/background.jpg` (Dead/LevelDone/PreBoss), comic textures (StartPack/EndPack), plus a solid `blackLayer` dim overlay.
- Loading: `GameAssets.assetsLoaded()` (:350-360) sets a stretched 1280×800 ortho; `drawLoadingScreen()` (:377-382) stretch-draws `loading/loading_back.jpg` and draws the bar at fixed (270,225).
- Desktop launcher: `desktop/src/com/xsheetgames/desktop/DesktopLauncher.java:17` `cfg.setWindowedMode(960, 640)`; window resizable.
- `menuScreen2.jpg` is currently untracked in git.

## Implementation steps

### 1. Shared cover helper — new `core/src/com/xsheetgames/genericElements/CoverLayout.java`

Unit-agnostic (screen pixels for menus, meters for parallax):

```java
public final class CoverLayout {
    public static final float LEFT = 0f, BOTTOM = 0f, CENTER = 0.5f, RIGHT = 1f, TOP = 1f;
    public static void apply(Sprite s, float nativeW, float nativeH,
                             float viewW, float viewH, float anchorX, float anchorY) {
        float scale = Math.max(viewW / nativeW, viewH / nativeH);
        float w = nativeW * scale, h = nativeH * scale;
        s.setSize(w, h);
        s.setPosition((viewW - w) * anchorX, (viewH - h) * anchorY);
    }
    // overload without anchors = CENTER/CENTER default
}
```

Anchor(LEFT, TOP) ⇒ x=0 (crop right), top edge pinned at viewH (crop bottom) — the menu requirement. Default overload = center/center for everything else.

### 2. Shared UI-viewport plumbing in `screens/AbstractScreen.java`

Add `protected Viewport uiViewport` plus helpers, mirroring what MenuScreen does inline (MenuScreen:42-74, 84-86, 104-105, 293-297); then refactor MenuScreen onto them so there is one implementation:

- `setupUiViewport()` — `new FitViewport(TARGET_WIDTH, TARGET_HEIGHT)` + update; call from `show()`.
- `updateUiViewport(w, h)` — call from `resize()`.
- `unprojectUi(x, y)` → `Vector2` via `uiViewport.unproject` (takes raw y-down screen coords — a drop-in replacement for the old stretch formula; taps in letterbox margins fall outside 0..1280/0..800 and simply miss all hit tests).
- `beginScreenPass(batch)` — full-window glViewport + `setToOrtho2D(0,0,screenW,screenH)` for cover backgrounds.
- `beginUiPass(batch)` — `uiViewport.apply(true)` + camera projection.
- `endScreenRender()` — restore full glViewport.

### 3. Pop-in fix — `genericElements/Level.java` `advanceLevel()` (~lines 81-84)

Convert the authored absolute x into an offset past the *actual* right edge, **at spawn time** (GAME_WORLD_WIDTH changes on desktop resize, so do not convert at parse time):

```java
PowerupDescription pw = this.powerups.pop();
// JSON x is authored against the fixed 20m design width (24/25 = 4/5m past the
// old right edge). Re-anchor to the actual, aspect-dependent world edge.
float spawnX = Configuration.GAME_WORLD_WIDTH + Math.max(pw.x - Configuration.VIEWPORT_WIDTH, 0.5f);
Chili.spawnChili(spawnX, pw.y, gameScreen.getActualPowerups(), gameScreen.getActualWorld(), this);
```

Preserves the designed 4 m/5 m lead-in on every aspect; no JSON edits; parse code (`AbstractLevelpack.readLevelFromFile`) unchanged. Random chili (Level.java:89) and all enemy/obstacle spawn sites (`JungleEnemies:82`, `JungleObstacles:222/227`, `BatMineEnemies:65`, `BatMineObstacles:312/318`) already use `GAME_WORLD_WIDTH + 5f` — no change.

### 4. New menu background (menuScreen2.jpg, crop right/bottom)

- `GameAssets.loadMenuAssets()` (:180) and `unloadMenuAssets()` (:195): swap `menuScreen.jpg` → `menuScreen2.jpg`.
- `MenuScreen.doAssetProcessing()` (:145-150): fetch `menuScreen2.jpg` (bgWidth/bgHeight become 1280×720).
- `MenuScreen.render()` (:47-58): replace the inline center/center cover math with `CoverLayout.apply(screenBackground, bgWidth, bgHeight, screenW, screenH, CoverLayout.LEFT, CoverLayout.TOP)` inside `beginScreenPass`.
  Result: 16:9 exact fit; 16:10 and 4:3 crop the RIGHT; 21:10 crops the BOTTOM.
- `git add android/assets/menu/images/menuScreen2.jpg` (currently untracked). Keep `menuScreen.jpg` on disk (unreferenced).

### 5. Migrate the 10 fixed-ortho screens (mechanical, per screen)

Template: `show()` adds `setupUiViewport()`; `resize()` (currently empty) calls `updateUiViewport(w,h)`; `render()` replaces the single `setToOrtho2D(0,0,TARGET_WIDTH,TARGET_HEIGHT)` with pass 1 (`beginScreenPass` → cover-draw background center/center + stretch the solid `blackLayer` over the whole window so cover margins get dimmed too) and pass 2 (`beginUiPass` → all existing sprite/font draws unchanged), then `endScreenRender()`; touch handlers replace the stretch formula with `unprojectUi(x, y)`. Remove per-screen `screenBackground.setSize(...)` in show() (CoverLayout sets size each frame — resize-proof).

| Screen | ortho line | background | touch lines |
|---|---|---|---|
| ChooseLevelpackScreen.java | 38 | credits_back.jpg (:72-76) | 180-208 |
| ChooseLevelScreen.java | 52 | credits_back.jpg (:124-127) | ~341-360 (fixes its int-division bug) |
| SettingsScreen.java | 53 | credits_back.jpg (:130-134) | 377-381 |
| InputScreen.java | 34 | credits_back.jpg (:68-71) | 171 |
| CreditsScreen.java | 33 | credits_back.jpg (:68-71) | 155 |
| DeadScreen.java | 57 | background.jpg (:93-94) + blackLayer :108-109 | 213 |
| LevelDoneScreen.java | 62 | background.jpg (:163-164) + blackLayer :195 | 364 |
| PreBossScreen.java | 39 | background.jpg (:82-85) | none (whole-screen tap) |
| StartPackScreen.java | 36 | comic (:86-87) | none |
| EndPackScreen.java | 33 | comic (:89) + menu sprite in UI pass | none |

Note: comic screens get center/center cover per the stated default; if comic content gets cropped too aggressively on 21:10, switching those two to contain/letterbox is a one-line follow-up.

### 6. Loading screen — `GameAssets.java` `assetsLoaded()`/`drawLoadingScreen()` (:350-382)

Switch the ortho to real window pixels; cover-draw `loading_back.jpg` (center/center, inline math — it's a Texture, not a Sprite); draw the loading-bar frame fit-scaled: `fit = min(w/1280, h/800)`, offset `(w−1280*fit)/2, (h−800*fit)/2`, bar at `(ox+270*fit, oy+225*fit)` sized `region*fit`. Fixes loading for every screen (all go through `assetsLoaded()`).

### 7. Parallax scrolling coverage — `genericElements/ParallaxLayer.java` `doLogic()` (:50-68)

Rewrite with rightmost-edge tracking (drop the `doElement` flag):

```java
if (isStatic) { for (Sprite s : layerElements) applyStaticCover(s); return; }
float nativeWidth = Configuration.VIEWPORT_WIDTH * layerTexture.getWidth() / Configuration.TARGET_WIDTH;
float rightEdge = 0f;
// move all elements left by layerVelocity*delta, remove those fully off-left
// (free to pool), track rightEdge = max(x + width); then:
while (rightEdge < Configuration.GAME_WORLD_WIDTH) { spawnLayerElement(rightEdge, 0f); rightEdge += nativeWidth; }
```

- Guarantees seamless tiles out to the actual world width on every aspect; new tiles always append at/after the visible edge (no pop-in); self-heals on desktop resize.
- Extract the static-cover block of `spawnLayerElement` (:88-95) into `applyStaticCover(Sprite)` using `CoverLayout.apply(..., CENTER, CENTER)` — dedupes with step 1 and makes static layers resize-safe too.
- Tidy the constructor capacity hints (:24/:26 contain a no-op `VIEWPORT_WIDTH / VIEWPORT_WIDTH`) to a plain small constant.

### 8. Hardcoded world-edge fixes

1. `levelpacks/batmine/BossBat.java:233`: `pos.x >= 16.6f` → `pos.x >= Configuration.GAME_WORLD_WIDTH - 3.4f` (today on 4:3 the boss sits at the screen edge; on 21:10 it stops 10 m short of it).
2. `levelpacks/jungle/BossSnake.java:499`: `pos.x >= 13.5f` → `Configuration.GAME_WORLD_WIDTH - 6.5f` (keep the left-edge check at :495).
3. `genericElements/BossEnergyMeter.java:21/27/54/60`: `920f`/`936f` → `Configuration.GAME_PIXEL_WIDTH - 360f` / `- 344f` (matches the distance-track anchor at GameScreen.java:192).

### 9. Edge cases

- **4:3 (world ≈ 16.67 m < 20 m)**: powerups spawn at 16.67+4/5 — off-screen; GameScreen HUD verified to fit at ~1066 px once the energy meter is anchored; the boss fixes keep bosses on-screen.
- **Desktop live resize mid-level**: spawn-time conversion (step 3) and per-frame tiling (step 7) read the current GAME_WORLD_WIDTH — safe. Boundary walls are built once in `GameScreen.show()`; resizing wider mid-level leaves the right wall inside — pre-existing behavior, out of scope (flag only).
- 16:10 must be a pixel-identical regression baseline: cover scale = 1, letterbox = 0, spawnX = 20+(25−20) = 25 — exactly the old behavior.

## Verification (desktop)

`gradlew desktop:run`, temporarily setting `cfg.setWindowedMode(w,h)` in `DesktopLauncher.java:17` for each of: **1024×768** (4:3), **1280×720** (16:9), **1280×800** (16:10 regression reference), **1680×800** (21:10). Per size:

1. Loading screen: background covers, bar undistorted.
2. Menu: menuScreen2 covers; pixel-exact at 1280×720; right side cropped on 4:3/16:10; bottom cropped on 21:10; buttons undistorted and clickable — also after live window resizing.
3. Every migrated screen (levelpack → level select → settings → input → credits; via play: start-pack comic, pause, dead, level-done, pre-boss, end-pack): backgrounds cover, nothing stretched, every button reacts exactly under the cursor (an offset button = a missed `unprojectUi` migration).
4. Pop-in: BatMine/Jungle level 1 at 1680×800 — chilis, enemies, obstacles slide in from off the right edge, never materialize in view; chilis arrive at the same rhythm as at 1280×800 and none vanish at birth.
5. Parallax: watch middle+front layers ≥60 s at 1680×800 — no gap at the right edge; resize wider mid-level → filled within a frame.
6. Bosses (level 15 of each pack): BossBat stops ~3.4 m, BossSnake ~6.5 m from the right edge on every aspect; energy meter fully visible at 1024×768.
7. 1280×800 run looks identical to the pre-change build.
8. Android sanity run (one 16:9 device) if available.

## Suggested order

CoverLayout + AbstractScreen helpers → MenuScreen refactor + menuScreen2 swap → loading screen → 10 screen migrations → Level.java powerup offset → ParallaxLayer rewrite → boss/meter fixes → verification matrix → commit this file + `menuScreen2.jpg`.