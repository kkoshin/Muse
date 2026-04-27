# Implementation Plan: Extract `fancy` Module for Caption Rendering

## Phase 1: Module Creation and Configuration [checkpoint: ccd2652]
- [x] Task: Create a new Gradle module directory `fancy` in the project root.
- [x] Task: Create `build.gradle.kts` for `fancy` configuring it as a Kotlin Multiplatform module (Android, iOS, Desktop) with Compose support.
- [x] Task: Include the `:fancy` module in `settings.gradle.kts`.
- [x] Task: Add `:fancy` as a dependency in the `:Playground` module's `build.gradle.kts`.
- [x] Task: Conductor - User Manual Verification 'Phase 1: Module Creation and Configuration' (Protocol in workflow.md)

## Phase 2: Core Data Models Migration
- [ ] Task: Define a `FancyConfig` data class in `fancy` to encapsulate `referenceWidth`, `referenceHeight`, and `referenceFontSize`.
- [ ] Task: Move `Caption.kt` (including `Caption`, `CaptionStyle`, `CaptionSegment`, `CaptionTransform`) from `Playground` to `fancy`'s `commonMain`.
- [ ] Task: Refactor data models in `fancy` to remove dependencies on `Constants.REFERENCE_WIDTH` etc., and use `FancyConfig` instead.
- [ ] Task: Move `CaptionProcessor.kt` from `Playground` to `fancy`'s `commonMain`.
- [ ] Task: Move `CaptionStyleTest.kt` and `CaptionProcessorTest.kt` from `Playground` to `fancy`'s `commonTest`.
- [ ] Task: Update tests to pass `FancyConfig` parameters correctly and verify they pass.
- [ ] Task: Conductor - User Manual Verification 'Phase 2: Core Data Models Migration' (Protocol in workflow.md)

## Phase 3: Rendering Logic Migration
- [ ] Task: Move `CaptionRender.kt` (`drawCaption`) from `Playground` to `fancy`'s `commonMain`.
- [ ] Task: Refactor `drawCaption` to accept `FancyConfig` instead of relying on global `Constants`.
- [ ] Task: Create `FancyCaptionView` Composable in `fancy`'s `commonMain` that encapsulates the `Canvas` and `drawCaption` logic.
- [ ] Task: Conductor - User Manual Verification 'Phase 3: Rendering Logic Migration' (Protocol in workflow.md)

## Phase 4: Playground Refactoring
- [ ] Task: Refactor `ExportManager.kt` in `Playground` to use the `fancy` module's models and rendering functions for generating bitmaps, passing the necessary `FancyConfig`.
- [ ] Task: Refactor `CaptionView.kt` in `Playground` to use `FancyCaptionView` instead of calling `Canvas` directly.
- [ ] Task: Remove any unused code or imports related to the migrated files in `Playground`.
- [ ] Task: Conductor - User Manual Verification 'Phase 4: Playground Refactoring' (Protocol in workflow.md)