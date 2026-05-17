# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Build debug Android APK
./gradlew assembleDebug

# Build release APK (prod build)
./gradlew assembleRelease

# Run all tests (shared module)
./gradlew :muse:test

# Run a single test class
./gradlew :muse:test --tests "io.github.kkoshin.muse.feature.isolation.AudioIsolationViewModelTest"

# iOS build (via CocoaPods)
./gradlew :muse:linkDebugFrameworkIosArm64

# Run on iOS simulator
./run_ios.sh
```

## Tech Stack

- **Kotlin Multiplatform (KMP)** — shared code between Android/iOS
- **Compose Multiplatform 1.11.0** — shared UI with `top.yukonga.miuix.kmp` (Miuix) component library
- **Koin 4.1** — dependency injection
- **Ktor 3.4** — HTTP client for ElevenLabs API
- **SQLDelight 2.3** — local database
- **AndroidX DataStore** — key-value preferences
- **Kotlinx Serialization** — JSON models
- **Kotlin 2.3.10** / **AGP 8.12.0** / **Java 17**

## Project Structure

```
MuseRoot/
├── composeApp/              # Android app entry point (MainActivity)
│   └── src/main/java/io/github/kkoshin/muse/app/
│       ├── App.kt
│       └── MainActivity.kt  # Sets up edge-to-edge, ModalBottomSheetLayout, NavHost
├── muse/                    # Shared KMP module (UI + domain + data)
│   └── src/
│       ├── commonMain/kotlin/io/github/kkoshin/muse/
│       │   ├── MainScreen.kt            # Top-level NavHost with all routes
│       │   ├── LocalNavController.kt     # DI-friendly nav wrapper
│       │   ├── designsystem/
│       │   │   ├── theme/AppTheme.kt     # Miuix theme wrapper (colorScheme, textStyles)
│       │   │   └── component/ScreenScaffold.kt  # Shared scaffold with TopAppBar
│       │   ├── feature/                   # Feature-based screen organization
│       │   │   ├── dashboard/             # Project list screen
│       │   │   ├── editor/                # Script editor screen
│       │   │   ├── export/                # Audio export pipeline & screen
│       │   │   ├── isolation/             # Audio isolation (noise removal)
│       │   │   ├── noise/                 # White noise generation
│       │   │   └── setting/               # Settings & voice picker
│       │   ├── core/
│       │   │   ├── manager/               # Business logic (ElevenLabProcessor, AudioIsolationProcessor, etc.)
│       │   │   └── provider/              # Service interfaces (TTSProvider, STTProvider, etc.)
│       │   ├── repo/                      # SQLDelight + DataStore repos
│       │   └── platformbridge/            # expect/actual declarations
│       ├── androidMain/                    # Android-specific implementations
│       ├── commonTest/                     # Unit tests (Kotlin Test)
│       └── commonMain/sqldelight/          # SQLDelight .sq files
├── elevenlabs/               # ElevenLabs API client (KMP module)
│   └── src/commonMain/kotlin/io/github/kkoshin/elevenlabs/
│       ├── ElevenLabsClient.kt            # Main API client
│       ├── api/                           # API endpoints (TextToSpeech, Voices, etc.)
│       └── model/                         # API response models
└── swiftApp/                 # iOS Xcode workspace
```

## Architecture Patterns

### Navigation
Type-safe routes using `@Serializable` data classes/objects (e.g., `@Serializable object DashboardArgs`). All routes are defined in `MainScreen.kt` via `NavHost`. A `LocalNavigationController` CompositionLocal provides non-Composable navigation support.

### Screen Pattern
Each feature screen follows this structure:
1. **Args** — `@Serializable` data class/object for navigation arguments
2. **Screen composable** — receives callbacks, uses `ScreenScaffold` for layout, `koinViewModel()` for state
3. **ViewModel** — extends `androidx.lifecycle.ViewModel`, exposes `StateFlow`

### UI Theme
All screens use `AppTheme` (wraps `MiuixTheme`) and reference `AppTheme.colorScheme.*` / `AppTheme.textStyles.*`. The shared `ScreenScaffold` wrapper provides a consistent MIUI-style TopAppBar layout.

### Platform Bridge
Platform-specific functionality uses Kotlin expect/actual:
- Platform.kt (expect) → Platform.android.kt / Platform.ios.kt (actual)
- Patterns: MP3 encoding/decoding, file picker, audio playback, back handler, document picker, logging

### Dependency Injection
Koin modules defined in platform-specific `appModule.kt` files (e.g., `androidMain/kotlin/.../appModule.kt`). ViewModels injected via `koinViewModel()`.

### Testing
- Kotlin Test in `muse/src/commonTest/`
- Coroutines tested with `runTest` + `kotlinx-coroutines-test`
- Dependencies mocked via fake implementations (see `AudioIsolationViewModelTest` for pattern)

### Database
SQLDelight schema in `commonMain/sqldelight/io/github/kkoshin/muse/database/Script.sq`. Currently single table `Script` with fields: id (TEXT PK), title, text, created_At.

## Key Development Notes

- **Kotlin 2.3** — uses `-Xexpect-actual-classes` compiler flag, `@OptIn(ExperimentalUuidApi::class)` for `kotlin.uuid.Uuid`
- **Miuix 0.9.1** — MIUI-style components at `top.yukonga.miuix.kmp.*`
- **Release builds**: Triggered by GitHub Release creation, signed via `ilharp/sign-android-release` action
- **Version**: 0.2.0 (code 6), `composeApp/build.gradle.kts` sets version values
- **iOS**: CocoaPods integration with `lame` pod for MP3 encoding
- **Branch**: `feature/adopt_miuix` — currently migrating UI components to Miuix
