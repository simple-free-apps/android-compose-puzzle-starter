# ChromaCircuit Starter — Android Kotlin & Jetpack Compose Puzzle Template (5 Levels)

A clean, modern **5-Level Conduit Puzzle Game Starter** built 100% with **Kotlin**, **Jetpack Compose (Material 3)**, and procedural **Compose Canvas** rendering — designed to let Android developers inspect and run a real dual-flavor game architecture in minutes.

![ChromaCircuit Gameplay Preview](docs/demo.gif)

## Included in This Free 5-Level Starter
- **5 Curated Levels (3x3 to 5x5):** Graph-connectivity BFS engine (`PuzzleEngine.kt`), par-move calculation, Hint solver (`findNextHint`), Undo stack, and 1–3 star progression.
- **100% Procedural Compose Canvas & Vector Art:** Zero raster PNG/WebP game assets or external font binaries; adaptive XML vector launcher icon (`ic_launcher_*.xml`).
- **100% Procedural Kotlin `AudioTrack` PCM SFX:** Synthesizes tap, conduit lock, undo, and victory chimes mathematically in pure Kotlin (`SoundEngine.kt`) — zero bundled `.ogg`/`.mp3`/`.wav` files.
- **Dual Build Flavors (`store` dimension):**
  - `noAds`: Compiles cleanly with zero AdMob SDKs, zero `INTERNET` permission, and zero `AD_ID` permission.
  - `withAds`: Google AdMob Banner, paced Interstitial, and Rewarded Hint integration wired with official Google **TEST IDs** only (`withAds/res/values/admob.xml`).
- **Full English & Arabic (RTL) Parity:** Every UI string is localized cleanly in `values/strings.xml` and `values-ar/strings.xml` with live in-app switching.
- **Pure-JVM Unit Tests:** `PuzzleEngineTest.kt` validates rotation symmetry, un-solved start states, and hint solvability for all 5 levels.

---

## More from Simple Free Apps

### Live published Android apps & games on Google Play

These are published apps, separate from the source projects listed below. The two flagship games are free to play and include optional in-app purchases and ads. Their source code is not for sale.

| App | Google Play |
|---|---|
| Chess With AI — chess against AI and online play | [Install](https://play.google.com/store/apps/details?id=com.gr8.games.chess.chesswithai&utm_source=android-compose-puzzle-starter&utm_medium=readme&utm_campaign=chesswithai) |
| Crazy Machines — physics and contraption puzzles | [Install](https://play.google.com/store/apps/details?id=com.gr8.games.crazymachines&utm_source=android-compose-puzzle-starter&utm_medium=readme&utm_campaign=crazymachines) |
| Inkear — offline speech to text | [Install](https://play.google.com/store/apps/details?id=com.gr8.app.inkear&utm_source=android-compose-puzzle-starter&utm_medium=readme&utm_campaign=inkear) |
| Liquid Sort Puzzle | [Install](https://play.google.com/store/apps/details?id=com.gr8.games.liquidsortpuzzle&utm_source=android-compose-puzzle-starter&utm_medium=readme&utm_campaign=liquid-sort) |
| Orvaleth: Clockwork Spires | [Install](https://play.google.com/store/apps/details?id=com.valkora.serenth.orvaleth&utm_source=android-compose-puzzle-starter&utm_medium=readme&utm_campaign=orvaleth) |

The source projects for Liquid Sort Puzzle and Orvaleth are also available on [Codester](https://www.codester.com/items/64630/liquid-sort-puzzle-kotlin-android-game-template?ref=Gr8Man) and [Codester](https://www.codester.com/items/76144/orvaleth-godot-3d-clockwork-puzzle-game?ref=Gr8Man). See the [Android games landing page](https://simple-free-apps.github.io/android-games/) for chess AI and physics contraption puzzle discovery.

### Apps and source projects

Browse the [Simple Free Apps catalog](https://simple-free-apps.github.io/?utm_source=android-compose-puzzle-starter&utm_medium=readme&utm_campaign=catalog) for the free starter, Android game source projects, and bundles. It links four Payhip products and ten Codester items:

| Project | Store |
|---|---|
| Kivorivane | [Payhip](https://payhip.com/b/WsHQp) · [Codester](https://www.codester.com/items/76093/kivorivane-1000-levels-kite-escape-kotlin?ref=Gr8Man) |
| Veyravel | [Payhip](https://payhip.com/b/Jv6fL) · [Codester](https://www.codester.com/items/76079/veyravel-3d-android-action-game-godot?ref=Gr8Man) |
| Orvaleth: Clockwork Spires | [Payhip](https://payhip.com/b/hWmcv) · [Codester](https://www.codester.com/items/76144/orvaleth-godot-3d-clockwork-puzzle-game?ref=Gr8Man) |
| Puzzle Pack 5-in-1 | [Payhip](https://payhip.com/b/beFfj) · [Codester](https://www.codester.com/items/76186/puzzle-pack-5-in-1-kotlin-android-game-bundle?ref=Gr8Man) |
| Liquid Sort Puzzle | [Codester](https://www.codester.com/items/64630/liquid-sort-puzzle-kotlin-android-game-template?ref=Gr8Man) |
| Zelquavex | [Codester](https://www.codester.com/items/76106/zelquavex-1-200-circuit-puzzles-android?ref=Gr8Man) |
| Tessavora | [Codester](https://www.codester.com/items/76001/tessavora-mosaic-puzzle-android-game?ref=Gr8Man) |
| PillarGlide | [Codester](https://www.codester.com/items/75998/pillarglide-tactical-slide-game-kotlin?ref=Gr8Man) |
| LanternLoom | [Codester](https://www.codester.com/items/75956/lanternloom-light-path-puzzle-game-for-android?ref=Gr8Man) |
| Trelovix | [Codester](https://www.codester.com/items/76112/trelovix-500-levels-block-puzzle-kotlin?ref=Gr8Man) |

Store listings control their current availability, price, and license terms. Some links may be affiliate links; any affiliate relationship should be disclosed on the destination page.

### Inkear offline transcription

[Inkear: Offline Speech to Text](https://play.google.com/store/apps/details?id=com.gr8.app.inkear&utm_source=android-compose-puzzle-starter&utm_medium=readme&utm_campaign=inkear) is a separate Android app for on-device English and Arabic transcription. The publisher describes no cloud upload and no monthly subscription. Its regional one-time price is shown in Google Play and may change.

### Google Play publishing assistance

For eligible, policy-compliant apps and active developer accounts, [ask about the $45 publishing and transfer assistance service](https://simple-free-apps.github.io/play-publish/?utm_source=android-compose-puzzle-starter&utm_medium=readme&utm_campaign=publishing-service). This service does not bypass testing, production-access requirements, account eligibility checks, or Google review; eligibility is checked first and no approval or timing is guaranteed. Certain newly created personal accounts must meet Google's closed-testing requirement before applying for production access.

### Suggested Awesome-list submissions

These are draft suggestions, not claims that a PR has been accepted. Confirm each repository's current contribution instructions before submitting.

**androiddevnotes/awesome-jetpack-compose-android-apps** — proposed section: Games / Open-source projects

> **ChromaCircuit Starter** — A free five-level Android conduit puzzle game starter built with Kotlin and Jetpack Compose. Includes English and Arabic RTL support, procedural Canvas visuals and sound, and ad-free / test-ad build flavors. [Source and APK](https://github.com/simple-free-apps/android-compose-puzzle-starter).

**jetpack-compose/jetpack-compose-awesome** — proposed section: Apps / Games

> [ChromaCircuit Starter](https://github.com/simple-free-apps/android-compose-puzzle-starter) — Open-source Kotlin and Jetpack Compose conduit puzzle starter with five playable levels, Arabic RTL localization, and procedural visuals.

**Heapy/awesome-kotlin** — proposed section: Android projects

> [ChromaCircuit Starter](https://github.com/simple-free-apps/android-compose-puzzle-starter) — Kotlin + Jetpack Compose Android puzzle game starter; five levels, English/Arabic UI, and two build flavors.

---

## Screenshots

| Home & Level Select | Active Puzzle & Hint | Victory Dialog |
|---|---|---|
| ![Home](docs/screen_home.png) | ![Gameplay](docs/screen_play.png) | ![Victory](docs/screen_win.png) |

---

## Try the Prebuilt APK
Download the ready-to-install debug APK directly from the **[Releases](https://github.com/simple-free-apps/android-compose-puzzle-starter/releases)** tab (`ChromaCircuitStarter-noAds-debug.apk`).

---

## Build Locally
Requires current stable Android Studio (or Gradle 9.6 / JDK 17 / Android SDK 37+):

```bash
# Run unit tests on both flavors
./gradlew :app:testNoAdsDebugUnitTest :app:testWithAdsDebugUnitTest

# Build ad-free and AdMob-test APKs
./gradlew :app:assembleNoAdsDebug :app:assembleWithAdsDebug
```

> **Quick Package Rename:** Update `applicationId` and `namespace` in `app/build.gradle.kts`, and replace the official Google **TEST** IDs in `app/src/withAds/res/values/admob.xml` with your live AdMob IDs before production signing.

---

## Starter vs. Full Commercial Pack (Puzzle Pack 5-in-1 — $12)

> **Verified test evidence:** the commercial Puzzle Pack passed 57/57 unit tests, with QA across three Android device profiles. This free starter is a separate five-level sample and its included `PuzzleEngineTest.kt` covers its own five levels.

| Feature | ChromaCircuit Starter (This Repo) | Soravel: Puzzle Pack 5-in-1 (Full Studio Kit) |
|---|---|---|
| **Included Games** | 1 Sample Mechanic | **5 Full Distinct Puzzle Games** (LanternLoom, PillarGlide, Tessavora, Kivorivane, Trelovix) |
| **Total Levels** | 5 Curated Levels | **1,928+ Campaign Levels** + Endless, Daily, Workshop & AI Duel |
| **Audio** | Procedural Kotlin PCM Beeps | **34 Commercial-Plan In-App `.ogg` SFX & Music** (`CREDITS.md` terms) |
| **Verification** | JUnit Starter Tests | **57/57 Verified Unit Tests** + Multi-Device Redroid QA |
| **Reskin Docs** | README Quick Start | **Complete Offline EN & AR HTML Reskin Guides** |
| **Price** | Free (Non-Resale Starter) | **$12 Instant Download on Payhip** ([Get the full pack](https://payhip.com/b/beFfj?utm_source=github&utm_medium=readme&utm_campaign=chromacircuit_starter)) |

**Live Published Android Demos (from our catalog on Google Play):**
- [Liquid Sort Puzzle on Google Play](https://play.google.com/store/apps/details?id=com.gr8.games.liquidsortpuzzle&utm_source=github&utm_medium=readme&utm_campaign=chromacircuit_starter)
- [Orvaleth: Clockwork Spires on Google Play](https://play.google.com/store/apps/details?id=com.valkora.serenth.orvaleth&utm_source=github&utm_medium=readme&utm_campaign=chromacircuit_starter)

---

## License
Distributed under the **ChromaCircuit Starter Evaluation & Learning License** ([LICENSE](LICENSE)). You may study, run, and build a single substantially customized app, **but you may not resell, redistribute, or republish this template as-is on code marketplaces or app stores**.

