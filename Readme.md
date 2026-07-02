# Dracoo the Dragon

Dracoo is an Android/desktop side-scrolling game built with the **libGDX** framework and **Java**. All artwork is by Sebastian Stadler, development by Michael Wagner.

You fly Draco the dragon through themed level packs (currently **BatMine** and **Jungle**), dodging or shooting enemies and obstacles that scroll in from the right, collecting chili powerups to enter fire mode, and surviving 15 levels per pack including a boss fight at the end.

## Prerequisites

You should have [OpenJDK 1.8](https://adoptopenjdk.net/) installed and available on your path. Android Studio with the latest build tools and SDK is necessary for Android builds — create `local.properties` in the repo root with `sdk.dir=...` (gitignored) or set the `ANDROID_HOME` environment variable. I recommend IntelliJ IDEA with the libGDX plugin for development.

There are no automated tests in this project; verify changes by running the desktop build (fastest feedback loop) and, for anything Android-specific, an on-device/emulator check.

## App & logic overview

### Modules

A multi-module Gradle project — all game logic lives in `core`, each platform is a thin launcher around it:

- `core` — platform-agnostic game code (screens, entities, physics, level loading).
- `desktop` — LWJGL3 launcher, all native-integration hooks are no-ops (dev build).
- `android` — the real shipping platform: `DracooMainActivity` implements sharing/rating/URL-opening via Android `Intent`s, and gamepad support goes through the shared `gdx-controllers`-based input layer.

This is a single, fully-unlocked, ad-free edition — there's no demo/full-version split and no ad SDK.

### Screen flow

```
MenuScreen
  └─ ChooseLevelpackScreen (BatMine / Jungle)
       └─ ChooseLevelScreen (15-level grid, egg status per level)
            ├─ StartPackScreen (intro comic, level 1 only)
            ├─ GameScreen ──┬─ DeadScreen (retry / menu)
            │               └─ LevelDoneScreen (egg rating, next / retry / menu)
            └─ level 15 → PreBossScreen (egg-count gate) → GameScreen (boss) → EndPackScreen (outro comic) → MenuScreen
```
`SettingsScreen`, `CreditsScreen`, and `InputScreen` are reachable from the menu at any time.

### One level, end to end

1. The level pack parses `assets/<pack>/levels/<PackName>_NN.json` into sorted spawn timelines (enemies/obstacles/powerups/breaks, each keyed by a spawn time in seconds) and builds a `Level`.
2. On entry, the level's spawn timeline is fast-forwarded once so the screen is already populated instead of scrolling in from empty.
3. Every frame, due entries are popped off the timeline and spawned (from an object pool) at the right edge of the visible world; they then fly/scroll left under their own box2d velocity.
4. Physics run in a fixed 1/60s step decoupled from render framerate; collisions (damage, kills, scoring, powerup pickup, boundary bounce) are resolved by a single box2d `ContactListener`.
5. Off-screen objects are returned to their pool every step instead of being garbage-collected.
6. The level ends when its timer runs out (or, for the boss, when the boss dies) and routes to the death or level-complete screen; level-complete computes a 0–3 "egg" rating from enemies killed and persists it to on-device progress storage.

### What's distinctive about the engine

- **Data-driven levels** — spawn timelines are external JSON, not hand-coded per level; the same generic "drain the timeline, spawn from a pool" engine drives both level packs.
- **Object pooling everywhere** — every spawnable entity, including bosses, is recycled through a pool to avoid GC churn on mobile.
- **Fixed-timestep physics with interpolated rendering** — box2d always sees the same `dt` regardless of device framerate, with sprite positions smoothly interpolated between physics steps.
- **Arbitrary aspect-ratio support** — the play field's width (not height) adapts to the device's actual aspect ratio at runtime, with `object-fit: cover` backgrounds and a fixed-design-frame UI viewport layered on top so nothing stretches or distorts on any screen shape.

See `CLAUDE.md` for the full architecture writeup (per-class responsibilities, invariants, and known tech debt) aimed at whoever — human or AI — is about to modify this codebase.

## Build & run commands

Use the Gradle wrapper (`./gradlew` on macOS/Linux, `gradlew.bat` on Windows).

```bash
./gradlew desktop:run              # run the desktop build — fastest dev loop, start here
./gradlew android:installDebug android:run   # build, install, and launch on a connected device/emulator
./gradlew build                    # build everything (all modules)
./gradlew clean                    # remove all build output
```

### Production builds

```bash
./gradlew desktop:dist             # runnable fat jar -> desktop/build/libs
./gradlew android:bundleRelease    # signed .aab for Play Store upload
./gradlew android:assembleRelease  # signed + minified (R8/ProGuard) release APK
```

Release signing reads credentials from **`local.properties`** (gitignored):
```
RELEASE_STORE_FILE=keystore       # optional, defaults to the checked-in ../keystore
RELEASE_STORE_PASSWORD=...
RELEASE_KEY_ALIAS=...
RELEASE_KEY_PASSWORD=...
```
Without `RELEASE_STORE_PASSWORD` set, release builds still succeed but come out unsigned.

## Maintenance

- **Version bump**: update `Configuration.VERSION`/`VERSION_DATE` (`core/src/com/xsheetgames/Configuration.java`) *and* `versionCode`/`versionName` in `android/build.gradle` — keep both in sync.
- **Add a level**: author a new `<pack>/levels/<PackName>_NN.json` spawn timeline; register new pack names in `GameAssets.initLevelPacks()`.
- **Add a native/platform capability**: add the method to `com.xsheetgames.iNativeFunctions` and implement it in both `DesktopLauncher` and `DracooMainActivity` (stub it on desktop if not applicable there).
- **Debug logging**: toggle `Configuration.debugLevel` / `poolingInfos` / `contactInfos` / `spawnInfos` in `Configuration.load()` for verbose engine logging during development.
