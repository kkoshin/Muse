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

- Kotlin 2.3.10, Compose Multiplatform 1.10.3, AGP 8.12.0
- Koin 4.1 (DI), Ktor 3.4 (networking), SQLDelight 2.3 (database)
- Compile SDK 36, Min SDK 29, Target SDK 35
- iOS deployment target 16.0
- JDK 17 required

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

## Project Docs

The `conductor/` directory contains project management documentation:
- `tech-stack.md` — full technology decisions and rationale
- `workflow.md` — TDD workflow, commit conventions, quality gates
- `product.md` — product vision, target users, key features
- `code_styleguides/general.md` — Kotlin code style
