# Specification: Extract `fancy` Module for Caption Rendering

## Overview
Extract the `CaptionView` preview rendering and export logic from the `Playground` module into a standalone Kotlin Multiplatform (KMP) module named `fancy`. This new module will support Android, iOS, and Desktop. The `Playground` will retain its `StyleSlider` and other debugging UI components, acting as a desktop verification entry point that depends on `fancy`. The `fancy` module will be kept clean and independent of application-level concerns (like specific file paths or UI sliders).

## Functional Requirements
- **Multiplatform Module**: Create a new KMP module `fancy` targeting Android, iOS, and JVM (Desktop).
- **Core Data Models**: Move core models like `Caption`, `CaptionStyle`, `CaptionTransform`, `CaptionSegment`, and related formatting enums (`TextStyleOption`) to `fancy`.
- **UI Component**: Expose a high-level Composable function (e.g., `FancyCaptionView`) that encapsulates the Canvas drawing and handles its own visual transformations based on provided configurations.
- **Export Capabilities**: Provide an API (e.g., `exportToBitmap`) that generates and returns a `Bitmap` (or ImageBitmap) for a given `Caption` and `CaptionTransform`, decoupled from actual file I/O operations.
- **Configuration Injection**: Ensure that reference constants (like `REFERENCE_WIDTH`, `REFERENCE_HEIGHT`, and `REFERENCE_FONT_SIZE`) are injected into the `fancy` module via a configuration object rather than hardcoded within the module.

## Non-Functional Requirements
- **Clean Architecture**: The `fancy` module must not depend on `Playground` or specific application features.
- **Testability**: Ensure unit tests for the core models and rendering logic are migrated to `fancy` and run across all supported platforms.

## Acceptance Criteria
- [ ] A new KMP module named `fancy` is created and correctly configured in the Gradle build.
- [ ] Core data models and rendering logic (`Caption.kt`, `CaptionRender.kt`, `CaptionProcessor.kt`) are moved to `fancy`.
- [ ] A `FancyCaptionView` Composable is available and used by `Playground`.
- [ ] `ExportManager` in `Playground` delegates bitmap generation to `fancy` and handles file saving itself.
- [ ] Constants like `REFERENCE_WIDTH` are removed from `fancy`'s internal logic and passed as parameters.
- [ ] `Playground` desktop app runs successfully and all styling sliders update the preview correctly.
- [ ] Automated tests pass for both `fancy` and `Playground` modules.

## Out of Scope
- Modifying the existing UI layout or functionality of the `StyleSlider` in the `Playground`.
- Implementing new text styles or formatting options not currently present.
- Handling file I/O or platform-specific sharing within the `fancy` module.