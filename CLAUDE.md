# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

**Dracoo the Dragon** — a side-scrolling Android/desktop game built on **libGDX 1.9.11** in **Java 8**. A multi-module Gradle project; all game logic lives in `core` and each platform is a thin launcher. Artwork by Sebastian Stadler, development by Michael Wagner.

Requires **JDK 1.8** on PATH. Android builds need the Android SDK; create `local.properties` with `sdk.dir=...` (gitignored) or set `ANDROID_HOME`.

## Build & Run

Use the Gradle wrapper (`./gradlew` / `gradlew.bat`). Module names: `core`, `desktop`, `android`, `ios`, `html`.

```bash
./gradlew desktop:run            # run the desktop build (fastest dev loop — start here)
./gradlew desktop:dist           # build runnable fat jar -> desktop/build/libs
./gradlew android:assembleDebug  # build debug APK
./gradlew android:installDebug   # install on connected device/emulator
./gradlew android:run            # launch installed app via adb
./gradlew html:superDev          # GWT super dev mode for the web build
./gradlew build                  # build everything
./gradlew clean
```

There are **no automated tests** in this project — there is no test source set or test task. Verify changes by running `desktop:run`.

## Architecture

### Platform launchers implement `iNativeFunctions`
`core` is platform-agnostic. Anything platform-specific (ads, analytics, sharing, vibration context, controller polling) is abstracted behind `com.xsheetgames.iNativeFunctions`, implemented per platform:
- `desktop/...DesktopLauncher` (mostly no-op stubs)
- `android/...DracooMainActivity` (Chartboost ads, Google Analytics, real device behavior)
- `ios/...IOSLauncher`, `html/...HtmlLauncher`

The launcher passes itself into `new DracooGdxGame(nativeFunctions)`, which stores it as `GameAssets.nativ`. Core code reaches platform features only through `GameAssets.nativ.*`. **When adding a native capability, add the method to `iNativeFunctions` and implement it in all four launchers** (stub it where not applicable, as the others do).

### `GameAssets` — global static service locator
`GameAssets` (all static) owns the two libGDX `AssetManager`s (`manager` for game/menu assets, `loadingManager` for the loading screen), the box2d `BodyEditorLoader`, file handles for `settings.txt`/`progress.xml`/`errorlog.txt`, and the `levelPacks` list. Assets are loaded/unloaded in batches per phase (`loadMenuAssets`/`unloadMenuAssets`, `loadGameAssets`/`unloadGameAssets`) and fetched by path string via `fetchTexture`/`fetchSound`/`fetchTextureAtlas`/etc. Sound/music/vibration go through `GameAssets.playSound`/`playMusic`/`vibrate`, which respect the `Configuration` toggles.

### `Configuration` — all tuning & build flags as static fields
`com.xsheetgames.Configuration` is the single place for build/version flags (`fullVersion`, `useAds`, `adPartner`, `store`, Chartboost keys, GA tracker id), viewport constants, debug logging level, and runtime settings. `Configuration.load()` sets defaults; user settings are then overlaid from `dracoo_the_dragon/settings.txt` in `GameAssets.initStaticFiles()`. Changing store/ad-partner/demo behavior happens here, not scattered in code.

### Screen flow (`Game.setScreen`)
All screens extend `screens/AbstractScreen` (which adds abstract controller/touch event hooks: `stepBack`, `startPress`, `primaryPress`, `steerXAxis/YAxis`, `screenTouched/AfterTouched/WhileTouch`). Flow: `MenuScreen` → `ChooseLevelpackScreen` → `ChooseLevelScreen` → `StartPackScreen`/`PreBossScreen` → **`GameScreen`** → `LevelDoneScreen`/`DeadScreen`/`EndPackScreen`/`CreditsScreen`. `DracooGdxGame.render()` wraps `super.render()` in a try/catch that logs the stack trace to `errorlog.txt`, reports via analytics, and exits — exceptions during gameplay are swallowed at the top level.

### `GameScreen` — the gameplay engine (~1000 lines)
Owns the box2d `World`, `OrthographicCamera`, `SpriteBatch`, the Universal Tween Engine `TweenManager`, and all live game-object collections (Draco, enemies, obstacles, fireballs, powerups, boundaries, parallax layers). Uses a **fixed-timestep accumulator** (`FIXED_TIMESTEP = 1/60`, Allan Bishop's pattern) to decouple box2d stepping from render framerate. Collisions are handled by `genericElements/ObjectContactListener`.

### Level packs (`AbstractLevelpack` + `levelpacks/`)
A level pack (`levelpacks/batmine`, `levelpacks/jungle`) extends `genericElements/AbstractLevelpack` and bundles its assets, parallax layer textures, enemy/obstacle collections, boss, and per-level data. **Levels are data-driven**: `readLevelFromFile()` parses `assets/<pack>/levels/<pack>_NN.json` (enemies, obstacles, powerups, breaks, plus speed/seconds/music) into `EnemyDescription`/`ObstacleDescription`/etc. and builds a `Level`. To add content, add a level JSON and/or a new pack subclass; register pack names in `GameAssets.initLevelPacks()`. Player progress (eggs/locked status per level) is persisted as XML in `dracoo_the_dragon/progress.xml`, seeded from `assets/game/progress.xml`.

### Game objects: factory + pool + collection
Spawned entities extend `genericGameObjects/GameObject` (wraps a box2d `Body` built from a `BodyEditorLoader` JSON model + an `Animation`/`Sprite`). To avoid GC churn during gameplay, objects are recycled through `GameObjectPool` (backed by a `GameObjectFactory` subclass that knows how to `createObject()`). **Never `dispose()` a pooled object directly — set `object.setDisposing = true`** and the pool/collection frees it in the correct order. `GameObjectCollection` (and subclasses like `EnemyCollection`, `AbstractObstacleCollection`) manage the active set, update, draw, and pooling lifecycle. Each concrete type typically has a matching `*Factory`.

### Input
`Configuration.inputType` selects the scheme. Touch input, on-screen buttons (`genericElements/InputButton`/`InputButtonCollection`), and physical controllers all funnel into the `AbstractScreen` event hooks. Controller polling is platform-specific via `IControllerUtils` (`*/ControllerUtils`) obtained through `GameAssets.nativ.GetControllerUtils()`; core reads buttons/axes with the `GameAssets.KEY_*` / `AXIS_*` constants.

## Conventions & gotchas

- **Package is `com.xsheetgames`** (Android `applicationId` is also `com.xsheetgames`), despite folder/repo name "dracoo". Android launcher lives under `com.xsheetgames.dracoo`.
- Comments and some log strings are **in German** — keep that in mind when reading/searching.
- Vendored code under `core/src/aurelienribon/` (BodyEditorLoader, Tween Engine) is third-party; `libs/` holds extra jars pulled in via `fileTree`.
- The `html`/GWT build cannot use reflection or `String.format` in core — see the manual zero-padding in `AbstractLevelpack.readLevelFromFile()`. Keep core GWT-compatible.
- Bumping the app version touches both `Configuration.VERSION`/`VERSION_DATE` and `android/build.gradle` `versionCode`/`versionName`.
- `keystore` (signing) and the `organisation/` directory (binaries, graphic assets, working files) are checked in but are not source.
