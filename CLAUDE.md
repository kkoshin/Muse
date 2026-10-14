# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Muse is a Kotlin Multiplatform (KMP) Text-to-Speech app using Compose Multiplatform. It integrates with the ElevenLabs API for high-fidelity voice generation and targets Android (primary) and iOS.

## Build & Test Commands

```bash
# Build debug APK
./gradlew :composeApp:assembleDebug

# Build release APK (CI pipeline)
./gradlew assembleRelease

# Run all tests
./gradlew test

# Run tests for a specific module
./gradlew :muse:test

# Run a single test class
./gradlew :muse:test --tests "io.github.kkoshin.muse.feature.isolation.AudioIsolationViewModelTest"

# Build iOS framework (pre-release verification)
./gradlew :muse:linkReleaseFrameworkIosArm64

# Run iOS on simulator
./run_ios.sh
```

## Module Architecture

```
composeApp/   — Android app entry point (MainActivity, App composable, crash logging)
                Android-only: depends on :muse

muse/         — Core shared KMP module
                commonMain: UI screens, ViewModels, audio processing (MP3/WAV),
                            platform bridges, data layer (SQLDelight, DataStore)
                androidMain/iosMain/jvmMain: platform-specific implementations
                Uses Koin for DI, SQLDelight for DB, Ktor for networking

elevenlabs/   — ElevenLabs API client (KMP)
                Ktor-based HTTP client, Kotlinx Serialization models
                Depended on by :muse

fancy/        — Caption rendering library (KMP, Compose Multiplatform)
                Self-contained UI components for styled caption display
                Depended on by :muse

swiftApp/     — iOS Xcode project (Swift, CocoaPods)
                Consumes KMP framework from :muse module
                Podfile includes LAME dependency
```

## Key Technology Versions

- Kotlin 2.4.21, Compose Multiplatform 1.11.1, AGP 8.13.2, Gradle 9.2.1
- Koin 4.2 (DI), Ktor 3.5 (networking), SQLDelight 2.4 (database)
- Compile SDK 36, Min SDK 29, Target SDK 36
- iOS deployment target 16.0
- JDK 17 required

### Why some of these are held back

Several versions look stale but are pinned by AndroidX/Gradle metadata, not by choice.
Before bumping any of them, check the `aar-metadata.properties` inside the artifact —
the `minAndroidGradlePluginVersion` and `minCompileSdk` fields are hard gates:

- **Compose Multiplatform 1.11.1 / compileSdk 36 / AGP 8.x.** CMP 1.12 resolves to
  Jetpack Compose 1.12.x, which declares `minAgpVersion 9.1.0` and `minCompileSdk 37`.
  AGP 9 also drops KMP compatibility with `com.android.application`/`com.android.library`,
  which would force splitting `:composeApp` and break the F-Droid recipe.
- **Ktor 3.5.2.** 3.6.0 pulls okhttp 5.5.0, whose `okhttp-android` needs `minCompileSdk 37`.
- **androidx.lifecycle / lifecycleViewmodel 2.10.0.** The whole 2.11.0 line declares
  `minAgpVersion 9.1.0` and `minCompileSdk 37`.
- **AboutLibraries 14.2.1.** 15.x needs `minCompileSdk 37`.
- **Gradle wrapper 9.2.1.** fdroidserver's `gradlew.py` lags on distribution hashes.

`iosX64` is gone: CMP 1.11, navigation3 1.1.2 and the JetBrains lifecycle line stopped
publishing `ios_x64` variants, so only `iosArm64` and `iosSimulatorArm64` remain. On an
Intel host that means the iOS simulator can no longer be built locally.

## Design Patterns

- **MVVM**: Each feature screen has a corresponding ViewModel (e.g., `DashboardViewModel`, `EditorViewModel`, `ExportViewModel`)
- **Dependency Injection**: Koin modules in `appModule.kt` (platform-specific)
- **Repository pattern**: `MuseRepo` for data access, `MusePathManager` for file paths
- **Platform bridges**: `expect/actual` declarations in `platformbridge/` for platform-specific APIs (file picker, media store, back handler, etc.)
- **Processor abstraction**: Audio processing via `SpeechProcessorManager`, `AudioIsolationProcessor`, `ElevenLabProcessor`

## Testing Conventions

- Tests live in `commonTest/`, `iosTest/`, or `jvmTest/` source sets
- Use `kotlin.test.Test` and `kotlinx.coroutines.test.runTest`
- Focus on data layer (ViewModels, managers, repositories) — UI tests are excluded per project convention
- Fake/mock dependencies via manual implementations (see `FakeAudioIsolationProcessor` pattern)
- Prefer `Result<T>` return types for error handling in processor interfaces

## Release Process

Version is controlled in `composeApp/build.gradle.kts` (`setUpStableVersion`). The CocoaPods version in `muse/build.gradle.kts` must match. Changelogs go in `fastlane/metadata/android/en-US/changelogs/<versionCode>.txt`. The CI workflow (`.github/workflows/package.yml`) builds and signs release APKs on tag push.

Two Android artifacts with different build types: `assembleRelease` produces the APK for GitHub Releases / F-Droid (keeps xcrash crash logging), while `bundlePlay` produces the Google Play AAB (`play` build type, **no xcrash** — its prebuilt `.so` files are not 16 KB page aligned and Play rejects them; crashes are covered by Android vitals). The variant seam lives in `composeApp/src/android<BuildType>/kotlin/.../crash/CrashReporter.kt`.

## Universal Skills

The `.agents/skills/` directory contains universal AI agent skills. Read the `SKILL.md` files for workflow instructions (e.g., release process). See `.agents/skills/_index.md` for available skills.
