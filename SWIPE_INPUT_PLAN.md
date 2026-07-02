# Swipe Input Mode — Implementation Plan (inputType 3)

Status: **planned, not yet implemented**. This document is self-contained; execute it in a
fresh session without further context. Read the "Input" and "Widescreen" sections of
`CLAUDE.md` first.

## Goal

Add a third input scheme with **no visible on-screen button controls**: a directional swipe
steers Draco. While the finger stays down, the swipe's direction vector is applied exactly
like held button presses (left/right/up/down); lifting the finger releases all directions,
like releasing a button.

Swipe mode should still provide minimal gesture feedback while the steering finger is down:
a white semi-transparent anchor dot, a white semi-transparent dashed radius circle around the
anchor, and a white semi-transparent moving dot at the current steering-finger position.
This is feedback for the swipe vector, not an interactive button UI.

## Prior art already in the tree (do not re-discover)

The pre-modernization game had a swipe scheme as `inputType == 3`. Its leftovers are still
present and can be reused or must be worked around:

- `GameAssets.java:91` clamps `inputType` to 1/2 — the German comment ("alte settings.txt
  kann noch tote Schemata (Swipe etc.) enthalten") confirms swipe was mode 3. **This clamp
  must be widened or the new setting silently reverts on every app restart.**
- `SettingsScreen` already loads a `control3` sprite from the `controls3btn` region
  (exists in `android/assets/menu/images/menu_items.pack`, line ~124) but never draws it —
  the settings-button artwork for this mode probably already exists. Visually verify it
  actually depicts swiping before shipping.
- `GameScreen` fields `touchInputs` / `actualInputs` / `absolutInputStarts` (three
  5-element `Array<Vector2>`, one slot per pointer) are allocated in `show()`, nulled in
  `dispose()`, and **never read** — skeleton of the old per-pointer swipe tracking. This
  plan does NOT use them; they can stay as-is (or be removed in a separate cleanup).
- `Draco` already null-guards the `buttons` collection everywhere it is used
  (constructor `Draco.java:~141`, `enableFireMode`/`disableFireMode`,
  `processFireKey:~692`), so a mode that never creates an `InputButtonCollection` needs no
  Draco rendering changes.

## Mechanic ("floating joystick")

1. **Touch down** anywhere → that point becomes the anchor; the **first pointer down is the
   steering pointer** (track its pointer id). While that steering pointer is held, draw a
   small white semi-transparent dot at the anchor.
2. **Drag** → direction vector = current position − anchor. Once the vector leaves a
   **dead zone** (~4% of screen height — express thresholds as fractions of
   `Gdx.graphics.getHeight()` so they are DPI/resolution independent), quantize it to held
   button states with a **per-axis threshold**: a diagonal swipe sets e.g.
   `upPressed + rightPressed` together. Draco's motion logic already force-splits
   perpendicular combos (`Draco.java:~568-580`), so diagonals work for free.
3. **Finger lifted** → all four directions released. Treat `touchCancelled` the same as
   `touchUp` (Android sends cancel on app switch); note `InputManager.touchCancelled`
   currently ignores events.
4. **Trailing anchor** (build in from the start): clamp the anchor to within ~10% of screen
   height behind the finger — if the vector exceeds that radius, drag the anchor along.
   Without this, reversing direction (swipe up, then dodge down) requires dragging all the
   way back past the original touch point. Keep radius + dead zone as named constants for
   playtest tuning.
5. **Fire**: no visible fire button. When `Configuration.autoFire` is off, **any second
   finger held down = fire held** (steering finger keeps steering). With autoFire on,
   nothing changes — the existing setting stays meaningful.
6. **Swipe feedback visualization**:
   - Only draw while `Configuration.inputType == 3`, gameplay is active, and the steering
     pointer is currently held.
   - Draw the anchor dot at the current trailing anchor, not only at the original touch-down
     point. If the anchor trails because the finger exceeded the radius, the anchor dot and
     dashed circle move with that adjusted anchor.
   - Draw the dashed circle centered on the anchor with radius equal to the trailing-anchor
     radius. This circle represents the outermost allowed drag distance before the anchor
     starts following the finger.
   - Draw the moving dot at the steering pointer's current position. Because the trailing
     anchor is clamped, this dot should appear inside or on the dashed circle. The vector
     used for steering is visually the offset from the circle center / anchor dot to this
     moving dot.
   - Use white with alpha for all three elements. Suggested starting values:
     anchor dot radius ~1.0% of screen height, current-position dot radius ~1.2% of screen
     height, alpha ~0.35-0.55. Keep these as named constants for playtest tuning.
   - Do not draw a full joystick, stick texture, D-pad, or fire button. The indicator should
     be lightweight enough not to hide gameplay.

## Where the code goes

Home for the tracking is **`InputManager`** (`core/src/com/xsheetgames/InputManager.java`),
NOT `GameScreen`. Rationale: `InputManager` is the single input funnel, receives
`touchDown/touchDragged/touchUp` before forwarding to screens, and already merges keyboard +
controller in `pollButton()`. Swipe becomes a third source behind the same API — and because
polling then works on desktop, **mouse-drag swiping works in `desktop:run`**, which is the
only fast verification loop this project has.

### 1. `InputManager`

- When `Configuration.inputType == 3`, track in the touch callbacks: steering pointer id,
  anchor (trailing), current position, and a count/set of secondary pointers.
- `pollButton(KEY_LEFT/RIGHT/UP/DOWN)` additionally returns the quantized swipe state.
- `pollButton(KEY_PRIMARY)` additionally returns "second finger held".
- Add `resetSwipe()` (clear all swipe state) for `GameScreen` to call.
- Constants (`private static final float`, next to `AXIS_DEADZONE`): dead-zone fraction
  (~0.04f of screen height), trailing-anchor radius fraction (~0.10f), and visual dot
  radius / alpha values.
- Add read-only accessors for the visual overlay, for example:
  - `boolean isSwipeActive()`
  - `Vector2 getSwipeAnchorScreen(Vector2 out)`
  - `Vector2 getSwipeCurrentScreen(Vector2 out)`
  - `float getSwipeRadiusPixels()`
  Return `false` / leave `out` unchanged when no steering pointer is active. These getters
  should expose the same trailing anchor and current point used by `pollButton`, so the
  visual state cannot drift from the movement state.
- Screen y-axis points down in `InputProcessor` coordinates — mind the sign when mapping to
  up/down.
- Keep the internally tracked swipe coordinates in `InputProcessor` screen coordinates
  (`x` right, `y` down). Convert them only at the draw site when rendering into libGDX's
  y-up HUD projection.
- Tracking happens regardless of which screen is active (harmless: only Draco consumes the
  direction codes — verified, `pollButton` directional codes have no other consumers);
  staleness is handled by the `resetSwipe()` calls below.

### 2. `Draco.doMotionLogic` (`Draco.java:~509-524`)

Extend the polling gate so mode 3 polls **unconditionally**:

- Today: `if(inputType == 1 || inputType == 2)` AND (controller connected OR
  desktop/WebGL) → poll overwrites the touch-event-set flags each fixed step. That gate
  exists precisely because polling *overwrites*.
- New: for `inputType == 3`, run the poll block always (no platform/controller condition).
  With swipe state inside `pollButton`, the per-step overwrite is exactly right: flags come
  from one merged source (swipe OR keyboard OR controller) every fixed step, and touchUp →
  tracker cleared → poll false → clean release.
- Fire needs **no extra code**: line ~517 `firePressed = pollButton(KEY_PRIMARY)` picks up
  the second-finger state. `if(Configuration.autoFire) firePressed = true;` (line ~525)
  stays as-is.

### 3. `GameAssets.initStaticFiles` (`GameAssets.java:~91`)

Accept 3 as valid: `if(Configuration.inputType < 1 || Configuration.inputType > 3)
Configuration.inputType = 1;` and update the German comment (Swipe is alive again).
Side effect: ancient devices with a swipe-era `settings.txt` get their preference back —
intended.

### 4. `SettingsScreen`

- Cycle the control toggle 1 → 2 → 3 → 1 (`touchedEvent`, line ~320; currently a 1↔2
  ternary).
- In `render()`, add an `inputType == 3` block mirroring the existing 1/2 blocks: draw the
  already-loaded `control3` sprite plus a new string field `control3String = "Input: Swipe"`.
- Persistence via the existing `saveSettingState()` — no change needed there.

### 5. `GameScreen`

- **No touch-handler changes** — the `inputType == 1/2` blocks in
  `screenTouched`/`screenWhileTouch`/`screenAfterTouched` simply don't fire for mode 3.
- Add the swipe feedback drawing here, not inside `InputManager`. `InputManager` should only
  provide gesture state; `GameScreen` owns rendering.
- Render the feedback in the pixel/HUD pass, after the world pass and before pause/menu UI
  is drawn. The existing HUD projection is:
  `setToOrtho2D(0, 0, Configuration.GAME_PIXEL_WIDTH, Configuration.TARGET_HEIGHT)`.
  Convert screen coordinates from `InputManager` like the pause buttons already do:
  `hudX = screenX * Configuration.GAME_PIXEL_WIDTH / Gdx.graphics.getWidth()`;
  `hudY = Configuration.TARGET_HEIGHT - screenY * Configuration.TARGET_HEIGHT /
  Gdx.graphics.getHeight()`.
- Use a `ShapeRenderer` for the overlay. Create/dispose it alongside `SpriteBatch`.
  End the `SpriteBatch` before drawing shapes, set the same HUD projection matrix on the
  `ShapeRenderer`, enable blending, draw the shapes, then resume the batch if later HUD UI
  still needs drawing.
- Dashed circles are not provided directly by `ShapeRenderer`: implement them by drawing
  short line segments around the circle (for example 32-48 segments, drawing every other
  segment). This avoids adding a texture asset just for a debug-like gesture indicator.
- Draw order: dashed circle first, anchor dot second, current-position dot last. If desired,
  draw a very thin semi-transparent line from anchor to current position using the same
  color but lower alpha; the two dots plus circle are the required part.
- Call `GameAssets.input.resetSwipe()` in `show()` and in `endPause()` so a touch that
  started on the pause overlay (resume tap) or on a previous screen can't leak a stale
  direction into gameplay. (`doMotionLogic` doesn't run while paused, so pause-time swipes
  are otherwise inert.)
- Extend the gate in `pause(String)` (line ~442, `inputType == 1 || inputType == 2`) to
  include 3 — otherwise a `#`-style two-part level message would silently not pause in the
  new mode. No current level JSON uses `#`, but it's a one-token fix.

### 6. Untouched on purpose

- `InputButtonCollection` / `GameScreen.doAssetProcessing` (line ~604): for mode 3,
  `buttons` stays `null` → nothing drawn (draw call at line ~198 already null-guards) and
  Draco's null-guards cover the rest.
- `Configuration`: `inputType` is already a plain int; no new fields required.

## Edge cases (already handled by the design — keep them working)

- Steering finger lifts while fire finger still down → steering releases; remaining finger
  stays fire-only. **No anchor promotion** — keeps behavior predictable.
- Steering finger is held without moving → draw the anchor dot and dashed circle; the moving
  dot may overlap the anchor dot. Direction remains neutral until the dead zone is exceeded.
- Steering finger moves beyond the radius → update the trailing anchor first, then expose
  the updated anchor/current positions to rendering. The current-position dot should not
  appear far outside the dashed circle during normal drag updates.
- Controller connected on Android → controller input wins via the poll merge, same as
  modes 1/2 today (where on-screen buttons are hidden when a controller is present).
- `touchCancelled` clears everything (app switch mid-swipe must not leave a direction stuck).

## Verification (no automated tests in this repo)

1. `./gradlew desktop:run` — set input to Swipe in settings; steer with mouse drag
   (works thanks to the poll-based design); confirm keyboard arrows still steer and SPACE
   still fires in the same session; confirm the settings label/graphic cycles correctly.
   While dragging, confirm the anchor dot, dashed radius circle, and moving dot appear,
   follow the trailing-anchor behavior, and disappear immediately on mouse/touch release.
2. Settings round-trip: toggle to Swipe → quit → relaunch → still Swipe (this catches a
   missed `GameAssets.java:91` clamp).
3. `./gradlew android:installDebug android:run` on a device: real multi-touch — steer +
   second-finger fire (with autoFire OFF), reversal responsiveness (trailing anchor),
   pause/resume tap doesn't cause a phantom swipe, no button controls drawn during gameplay,
   and the swipe feedback is readable without obscuring Draco or hazards.
4. Playtest-tune the constants (dead zone, trailing radius, visual dot sizes/alpha/dash
   count) before committing final values.

## Expected footprint

~100–140 lines in `InputManager` + swipe getters; ~40–80 lines in `GameScreen` for
`ShapeRenderer` setup and dashed-circle drawing; one-to-three-line touches in `Draco`,
`GameAssets`, `SettingsScreen`, `GameScreen`. No asset changes expected (verify
`controls3btn` artwork).
