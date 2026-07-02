# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

**Dracoo the Dragon** — a side-scrolling Android/desktop game built on **libGDX 1.13.1** (`gdxVersion` in root `build.gradle`) in **Java 8**. A multi-module Gradle project; all game logic lives in `core` and each platform is a thin launcher. Artwork by Sebastian Stadler, development by Michael Wagner.

The project was modernized in 2026 (see commit "Modernize project: ad-free single-edition, modern toolchain & controllers"): it dropped ads/analytics/third-party SDKs and moved to `gdx-controllers` for gamepad support, and later gained arbitrary-aspect-ratio ("widescreen") support (see the **Widescreen / aspect-ratio system** section below). Older docs, comments, or forum posts describing Chartboost ads, Google Analytics, MOGA support, or an iOS/HTML build describe the **pre**-modernization codebase and no longer apply — see "Superseded/removed subsystems" below if you find references to them.

Requires **JDK 1.8** on PATH. Android builds need the Android SDK; create `local.properties` with `sdk.dir=...` (gitignored) or set `ANDROID_HOME`.

## Build & Run

Use the Gradle wrapper (`./gradlew` / `gradlew.bat`). Module names: `core`, `desktop`, `android` (that's all — see "Superseded/removed subsystems").

```bash
./gradlew desktop:run              # run the desktop build (fastest dev loop — start here)
./gradlew desktop:dist             # build runnable fat jar -> desktop/build/libs
./gradlew android:assembleDebug    # build debug APK -> android/build/outputs/apk/debug
./gradlew android:installDebug     # install debug APK on connected device/emulator
./gradlew android:run              # launch the already-installed app via adb (custom task in android/build.gradle)
./gradlew android:bundleRelease    # signed .aab for Play Store upload (needs local.properties signing props, see below)
./gradlew android:assembleRelease  # signed + ProGuard/R8-minified release APK
./gradlew build                    # build everything (all modules)
./gradlew clean
```

There are **no automated tests** in this project — there is no test source set or test task. Verify changes by running `desktop:run` (fastest) and, for anything touching Android-specific code (`DracooMainActivity`, controller mappings, manifest), also `android:installDebug android:run` on a device/emulator.

### Release signing

`android/build.gradle` reads signing credentials from **`local.properties`** (gitignored, not `keystore.properties`):
```
RELEASE_STORE_FILE=keystore       # optional, defaults to ../keystore (the checked-in file)
RELEASE_STORE_PASSWORD=...
RELEASE_KEY_ALIAS=...
RELEASE_KEY_PASSWORD=...
```
If `RELEASE_STORE_PASSWORD` is absent, release builds are simply unsigned (`hasReleaseSigning` short-circuits) — `assembleRelease`/`bundleRelease` still succeed, they just don't produce an installable/uploadable artifact.

### Maintenance / version bump checklist

1. Bump `Configuration.VERSION` / `Configuration.VERSION_DATE` (`core/src/com/xsheetgames/Configuration.java`).
2. Bump `versionCode` (increment) and `versionName` in `android/build.gradle`.
3. Keep both in sync — a mismatch (e.g. `Configuration.VERSION` ahead of `android/build.gradle`) means a release is mid-flight; check `git log`/working tree before assuming which is authoritative.
4. If assets changed, `android:assembleDebug` once locally to catch missing/renamed asset paths early (`GameAssets` fetch calls fail silently — see "Known tech debt" below).

## Architecture

### Platform launchers implement `iNativeFunctions`
`core` is platform-agnostic. `com.xsheetgames.iNativeFunctions` is now a small, five-method interface for OS-level chrome the core can't do itself: `showMessage`, `openURL`, `share`, `rate`, `more`. Implemented by:
- `desktop/...DesktopLauncher` — all no-op stubs (dev build has nowhere useful to send these).
- `android/...DracooMainActivity` — real `Intent`/`AlertDialog` behavior; no ad SDK, no analytics.

The launcher passes itself into `new DracooGdxGame(nativeFunctions)`, which stores it as `GameAssets.nativ`. Core code reaches these platform features only through `GameAssets.nativ.*`. **When adding a native capability, add the method to `iNativeFunctions` and implement it in both launchers** (stub it where not applicable, as `DesktopLauncher` does). Note **input/controller handling is not part of this interface** — see "Input" below.

### `GameAssets` — global static service locator
`GameAssets` (all static) owns the two libGDX `AssetManager`s (`manager` for game/menu assets, `loadingManager` for the loading screen), the box2d `BodyEditorLoader`, file handles for `settings.txt`/`progress.xml`/`errorlog.txt`, the `levelPacks` list, and the shared `InputManager` (`GameAssets.input`). Assets are loaded/unloaded in batches per phase (`loadMenuAssets`/`unloadMenuAssets`, `loadGameAssets`/`unloadGameAssets`) and fetched by path string via `fetchTexture`/`fetchSound`/`fetchTextureAtlas`/etc. — **these fetch methods return `null` if the asset isn't currently loaded rather than throwing**, so a typo'd/stale path fails silently at the call site, not at load time. Sound/music/vibration go through `GameAssets.playSound`/`playMusic`/`vibrate`, which respect the `Configuration` toggles.

### `Configuration` — all tuning & build flags as static fields
`com.xsheetgames.Configuration` is the single place for version info (`VERSION`/`VERSION_DATE`), viewport/world-size constants (see "Widescreen" below), debug logging level (`debugLevel`, `poolingInfos`, `contactInfos`, `spawnInfos`), and runtime settings (`soundEnabled`, `musicEnabled`, `vibrateEnabled`, `inputType`, `autoFire`). `Configuration.load()` sets defaults; user settings are then overlaid from `dracoo_the_dragon/settings.txt` in `GameAssets.initStaticFiles()`. There is no more ad-partner/store-variant switching (single ad-free edition) — that used to live here in the pre-modernization codebase.

### Screen flow (`Game.setScreen`)
All screens extend `screens/AbstractScreen` (which adds abstract controller/touch event hooks: `stepBack`, `startPress`, `primaryPress`, `steerXAxis/YAxis`, `screenTouched/AfterTouched/WhileTouch`, plus the shared UI-viewport helpers described under "Widescreen" below). Flow: `MenuScreen` → `ChooseLevelpackScreen` → `ChooseLevelScreen` → `StartPackScreen`/`PreBossScreen` → **`GameScreen`** → `LevelDoneScreen`/`DeadScreen`/`EndPackScreen`/`CreditsScreen`. `SettingsScreen` and `CreditsScreen` are reachable from the menu at any time. `DracooGdxGame.render()` wraps `super.render()` in a try/catch that logs the stack trace to `dracoo_the_dragon/errorlog.txt` and exits — **exceptions during gameplay are swallowed at the top level**, so a bug that would crash-log on desktop can instead silently soft-lock or reset the app on device; check `errorlog.txt` first when debugging a field report.

### `GameScreen` — the gameplay engine (~1000 lines)
Owns the box2d `World`, `OrthographicCamera`, `SpriteBatch`, the Universal Tween Engine `TweenManager`, and all live game-object collections (Draco, enemies, obstacles, fireballs, powerups, boundaries, parallax layers). `render()` does, per frame: asset/prefill bookkeeping → (if unpaused) input polling + a **fixed-timestep accumulator** loop (`FIXED_TIMESTEP = 1/60`, Allan Bishop's pattern, capped at `MAX_STEPS = 5` box2d steps/frame to avoid a spiral of death) → non-physics per-frame logic (level timeline, parallax) → world-space draw pass (camera-projected, meters) → pixel-space HUD draw pass (`GAME_PIXEL_WIDTH × TARGET_HEIGHT` ortho) → end-of-frame transitions (dead/level-done/pack-done). Collisions are handled by `genericElements/ObjectContactListener`, which is a big `instanceof` dispatch table over `(FixtureA.userData, FixtureB.userData)` pairs — see its class comment for the collision matrix.

### Level packs (`AbstractLevelpack` + `levelpacks/`)
A level pack (`levelpacks/batmine`, `levelpacks/jungle`) extends `genericElements/AbstractLevelpack` and bundles its assets, parallax layer textures, enemy/obstacle collections, boss, and per-level data. **Levels are data-driven**: `readLevelFromFile()` parses `assets/<pack>/levels/<PackName>_NN.json` (enemies, obstacles, powerups, breaks, plus `speed`/`seconds`/`music`) into `EnemyDescription`/`ObstacleDescription`/etc. and builds a `Level`. `Level.advanceLevel()` walks these sorted-by-`ctime` arrays each frame and spawns whatever's due; `Level.prefillLevelStart()` (see "Widescreen" below) does the same "fast-forward" once at level start so the screen isn't empty on entry. To add content: author a new `<pack>_NN.json` (x/ctime authored against the fixed 20m/12.5m design frame, not the live aspect-dependent width) and/or a new pack subclass; register pack names in `GameAssets.initLevelPacks()`. Player progress (eggs/locked status per level) is persisted as XML in `dracoo_the_dragon/progress.xml`, seeded from `assets/game/progress.xml`.

### Game objects: factory + pool + collection
Spawned entities extend `genericGameObjects/GameObject` (wraps a box2d `Body` built from a `BodyEditorLoader` JSON model + an `Animation`/`Sprite`). To avoid GC churn during gameplay, objects are recycled through `GameObjectPool` (backed by a `GameObjectFactory` subclass that knows how to `createObject()`). **Never `dispose()` a pooled object directly — set `object.setDisposing = true`**, or better, call `object.free()` (routes to the pool if one is set, else disposes) — the pool/collection frees it in the correct order on the next `draw()`/`removeOutlaws()` pass. `GameObjectCollection` (and subclasses like `EnemyCollection`, `AbstractObstacleCollection`) manage the active set, update, draw, and pooling lifecycle; `removeOutlaws()` is what actually returns off-screen objects to their pool each fixed step. Each concrete type typically has a matching `*Factory`. `Draco` is a hand-built exception to the one-body-per-object rule: it's four separately-created box2d bodies (torso/legs/hands/head) joined with revolute + weld joints to fake a simple ragdoll, each with its own smoothing state — see `Draco.smoothStates`/`resetSmoothStates`.

### Input
`Configuration.inputType` selects the on-screen scheme: `1` = discrete steering buttons, `2` = a single D-Pad graphic with quadrant-based direction detection (`InputButtonCollection.getDpadDirection`); both are toggled in `SettingsScreen` and persisted in `settings.txt`. **All** input — touch, on-screen buttons/D-Pad, keyboard, and physical controllers — funnels through `com.xsheetgames.InputManager` (registered once as the libGDX `InputProcessor` + `Controllers` listener in `DracooGdxGame.create()`), which either dispatches discrete actions straight to the current screen's `AbstractScreen` hooks (menu navigation, pause) or exposes per-frame `pollButton(GameAssets.KEY_*)`/`pollAxis(GameAssets.AXIS_*)` for continuous steering (`Draco.doMotionLogic`). Controller support is the real `gdx-controllers` API (`ControllerAdapter`/`ControllerMapping`), not platform-specific code — there is no more per-platform `ControllerUtils`/MOGA SDK.

### Widescreen / aspect-ratio system
The game was originally authored for a fixed 16:10 canvas and was later adapted to fill arbitrary device aspect ratios without stretching. Key pieces:
- `Configuration.VIEWPORT_WIDTH`/`VIEWPORT_HEIGHT` (20m × 12.5m) are the **fixed design reference** — used only as the pixel↔meter ratio when sizing sprites (`spriteWidthMeters = VIEWPORT_WIDTH * regionPx / TARGET_WIDTH`). They never change at runtime.
- `Configuration.GAME_WORLD_WIDTH` (meters) / `GAME_PIXEL_WIDTH` (pixels) are the **actual live world extent**: height is pinned at the full 12.5m/800px, width is derived from the current screen aspect in `Configuration.updateGameViewport()`, called on startup and on `GameScreen.resize()`. The box2d camera, HUD ortho projection, and boundary walls (`BoundaryCollection`) all key off `GAME_WORLD_WIDTH`.
- All enemies/obstacles/random chilis spawn at `GAME_WORLD_WIDTH + 5f` and scroll left, so they always enter from just past the live right edge regardless of aspect. JSON-authored powerup `x` values are hand-authored against the old fixed 20m width and are re-anchored at spawn time in `Level.advanceLevel()`/`prefillLevelStart()` (`GAME_WORLD_WIDTH + max(jsonX - VIEWPORT_WIDTH, 0.5f)`) — **do not** "fix" the JSON values to the new width, the re-anchoring is intentional and keeps the authored lead-in distance.
- `Level.prefillLevelStart()` fast-forwards a level's spawn timeline once at level start so a wide viewport isn't empty for the first few seconds (skipped for boss levels — `seconds > 400` — and capped before the first `break`; nothing is placed left of `x = 11` to protect Draco's start position at `x = 8`).
- **Pause-visibility trap**: `GameObject.draw()` only draws once `firstTimeSmoothened == true`, and that flag is only set by `resetSmoothStates()`, which normally only runs inside the unpaused fixed-step loop. Anything spawned while `GameScreen.paused` (e.g. the level-start prefill, which happens behind the "press play" pause) is invisible until `GameScreen` calls `resetSmoothStates()` once manually — it does this right after `prefillLevelStart()`. The same trap applies to `ParallaxLayer`: `doLogic()` (which tiles up to `GAME_WORLD_WIDTH`) doesn't run while paused either, so `ParallaxLayer`'s constructor pre-tiles the full world width itself (`fillToWorldWidth`) instead of relying on the first `doLogic()` call. If you add another spawn-at-start code path, replicate this pattern or it will render nothing until the first unpause.
- UI screens use a shared 1280×800 `FitViewport` (`AbstractScreen.setupUiViewport`/`beginUiPass`/`unprojectUi`) over a full-window "cover" background pass (`beginScreenPass`); `MenuScreen` additionally anchors its buttons to the true screen corners. `genericElements/CoverLayout` is the shared `object-fit: cover` helper (scale-to-fill + crop, with a configurable anchor) used by both static backgrounds and the static `ParallaxLayer`.
- Full implementation notes and the original problem list live in `ASPECT_RATIO_PLAN.md` at the repo root — read it before touching viewport/spawn/HUD-layout code, it documents the "why" behind several non-obvious choices (e.g. why `Chili` is a `KinematicBody` and what that implies for `removeOutlaws()`).

## Superseded / removed subsystems

If you see a reference to any of the following (in old comments, docs, forum posts, or your own training data about this repo), it describes the **pre-2026-modernization** codebase and is gone from the current tree — don't try to "restore" or route through it:
- **`ios`/`html` Gradle modules** — removed. `settings.gradle` only includes `core`, `desktop`, `android`. GWT-compatibility constraints in `core` (no reflection, no `String.format`, manual zero-padding in `AbstractLevelpack.readLevelFromFile()`) are historical leftovers from when an HTML/GWT build existed; they're harmless to keep following but no longer strictly required.
- **Chartboost ads / Google Analytics / demo-vs-full-version split** — removed. This is a single, fully-unlocked, ad-free edition (see `Configuration.load()` comment). `iNativeFunctions` no longer has ad/analytics methods.
- **MOGA controller SDK / per-platform `IControllerUtils`+`ControllerUtils`** — removed, including the dead `core/src/com/xsheetgames/IControllerUtils.java` interface itself (zero references anywhere in the tree). Controller input is now `InputManager` + `gdx-controllers` (see "Input" above). The now-unused `menu/images/moga_pocket.png`/`moga_pro.png`/`ouya_controller.png` assets are still loaded/unloaded in `GameAssets.loadMenuAssets()`/`unloadMenuAssets()` but are not drawn anywhere — check before removing in case a screen references them by another path.

## Known tech debt (read before "fixing" it)

- `GameObject.getRightTop()` (`genericGameObjects/GameObject.java`) is flagged by its own TODO as not actually correct (sprite width is not the same as body width) — it's load-bearing for `removeOutlaws()`/`checkIfOutlaw()` off-screen culling across the whole codebase. Don't "fix" it without deliberately re-checking every caller; the current behavior is at least consistently wrong in a way the level designs already account for.
- Screens have many empty `// TODO Auto-generated method stub` method bodies (Eclipse-generated overrides of `AbstractScreen`'s abstract controller hooks that a given screen simply doesn't use, e.g. `steerXAxis` on a menu screen). These are intentional no-ops, not missing work.
- No automated tests and no CI — this repo relies entirely on manual verification (`desktop:run`) plus, for platform-specific changes, an on-device Android check. Be extra conservative with behavioral changes to `GameScreen`, `ObjectContactListener`, and the pooling lifecycle since regressions there are easy to introduce and hard to catch mechanically.
- **Global mutable statics used as ad hoc cross-cutting state**, beyond the `GameAssets` service locator documented above: `GameScreen.paused` (read directly by `Draco`, `Enemy`, `Obstacle`, `GameObject`, `AtlasAnimation`, `AbstractLevelpack`, and more, instead of being passed down), `GameScreen.bossEnergyMeter` (written from both level packs' enemy collections and boss classes), and `GameAssets.buttonTimer` (a single shared UI-debounce cooldown read/reset by ~10 screens instead of each screen owning its own). These work today but are easy to introduce a state-machine-shaped bug into — read every call site before touching, don't assume it's a single-writer field.
- `adShowed` fields (`MenuScreen`, `ChooseLevelScreen`, `DeadScreen`) are a leftover from a removed ad-SDK integration (see `DracooMainActivity`'s header comment) — set `true` unconditionally in `show()` and never toggled back. `ChooseLevelScreen.render()` gates its entire render body on `this.adShowed == true`, i.e. a permanently-true flag guarding real logic. Confusing but currently load-bearing; don't delete the conditionals without testing every affected screen.
- `BatMineObstacles`/`JungleObstacles` (`levelpacks/batmine`/`levelpacks/jungle`) constructors are ~30-40 near-identical `new GameObjectPool(...)` lines, and `spawnObstacle()` is a ~30-branch `if/else if` chain keyed by obstacle name that 1:1 maps a name to a pool field. A `Map<String, GameObjectPool>` would collapse this substantially, but it's the largest mechanical-duplication win in the codebase and touches every obstacle spawn path in both level packs — do it one file at a time with careful before/after diffing of every obstacle name, not as a drive-by change.
- `AbstractObstacleCollection.dispose()` does not call `super.dispose()` (unlike `GameObjectCollection.dispose()`, it never nulls/clears `objects`/`pools`) — an inconsistent override that isn't currently causing a visible bug (callers always replace the collection afterward) but is risky to "fix" without checking every disposal ordering assumption downstream.
- Box2D collision-filter `categoryBits`/`maskBits` are hardcoded raw `short`s per class (see the inline comments next to each declaration for the decoded bit table) rather than shared named constants — safe to read, risky to change without re-deriving every mask by hand.

## Conventions & gotchas

- **Package is `com.xsheetgames`** (Android `applicationId` is also `com.xsheetgames`), despite folder/repo name "dracoo". Android launcher lives under `com.xsheetgames.dracoo`.
- Comments and some log strings are **in German** (including several newer ones from the widescreen/input-modernization era) — keep that in mind when reading/searching. Larger explanatory doc comments added since (class/method-level architecture notes) are in English for broader readability; either is fine going forward, but don't rewrite an existing German comment into English as a drive-by change.
- Vendored code under `core/src/aurelienribon/` (BodyEditorLoader, Tween Engine) is third-party; `libs/` holds extra jars pulled in via `fileTree` (the Tween Engine jar — it's not on Maven Central).
- Bumping the app version touches both `Configuration.VERSION`/`VERSION_DATE` and `android/build.gradle` `versionCode`/`versionName` — see the "Maintenance / version bump checklist" above.
- `keystore` (signing) and the `organisation/` directory (`Binarys`, `Final Graphic Assets`, `Working Directory` — source graphic files, not consumed by the build) are checked in but are not source; `android/assets/` is what actually ships.
- Level content lives in `android/assets/<pack>/levels/<PackName>_NN.json` (note the capitalized pack name in the filename, e.g. `BatMine_01.json`, vs. lowercase `batmine` for the asset folder/package). `x`/`ctime`/`y` in these files are authored against the fixed 20m×12.5m design frame — see "Widescreen" above before editing spawn positions.
