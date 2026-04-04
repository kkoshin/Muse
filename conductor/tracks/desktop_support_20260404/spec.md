# Specification: Desktop Playground Support

## Overview
Create a standalone `Playground/` module to provide a desktop (JVM) experimental environment for testing and debugging isolated UI components from the `muse` module, specifically focusing on "Text Overlays". This module will be independent of the main `Muse` application's data layer (DB, network).

## Functional Requirements
- **Standalone JVM Entry Point**: A `Main.kt` in `Playground/` that launches a standard desktop window (1280x720).
- **Component Host**: A mechanism to easily load and render specific UI components from the `muse` module (initial focus: `Text Overlays`).
- **Basic JVM Stubs**: Implement minimal logic for platform-specific interfaces in the JVM target (e.g., `MusePathManager.jvm.kt` using JVM temp directories).
- **Mock Assets**: Support for desktop-specific mock assets and graceful fallback to placeholders for missing platform-specific resources.
- **Transitive JVM Targets**: Enable `jvm()` in `elevenlabs` and `muse` modules to support the `Playground` dependency.

## Non-Functional Requirements
- **Isolation**: The `Playground` module must NOT depend on or initialize the actual `AppDatabase` or real network clients.
- **Minimal Core Impact**: Changes to `muse` and `elevenlabs` should be limited to enabling the JVM target and providing no-op/basic stubs in `src/jvmMain`.
- **Zero Impact on Mobile**: Android and iOS builds must remain functional and untouched.

## Acceptance Criteria
- [ ] `./gradlew :Playground:run` launches a desktop window.
- [ ] A "Text Overlay" component from the `muse` module can be rendered and interacted with in the Playground window.
- [ ] Platform-specific calls (e.g., file path resolution) do not crash the JVM target due to missing stubs.
- [ ] No regressions in Android or iOS builds.

## Out of Scope
- Full application navigation or state persistence (SQLDelight/DataStore).
- Real ElevenLabs API integration or audio playback.
- Testing complex hardware-dependent features (e.g., audio recording/encoding).
