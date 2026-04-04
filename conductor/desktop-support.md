# Add Desktop Playground Support Plan

## Objective
Create a standalone `Playground/` module as an experimental desktop debugging environment. This allows for rapid UI component testing in isolation without integrating the full `muse` application or its data layers (DB, network).

## Key Architectural Decisions
- **`Playground/` Module**: New root-level module focusing ONLY on isolated UI component debugging.
- **Standalone Mode**: No integration with `Muse` main screens, database, or network requests.
- **Minimal Core Changes**: Enable `jvm()` targets in `muse` and `elevenlabs` ONLY to allow `Playground` to access shared UI components.

## Implementation Steps

### 1. Register New Module
- **`settings.gradle.kts`**: Include `:Playground`.

### 2. Enable JVM in `elevenlabs` (Minimal)
- **`elevenlabs/build.gradle.kts`**: 
  - Add `jvm()` target (required for transitively depending on it via `muse`).
  - Add `jvmMain` with minimal stubs if necessary.

### 3. Enable JVM in `muse` (UI Only)
- **`muse/build.gradle.kts`**:
  - Add `jvm()` target.
  - Add `jvmMain` source set.
- **Platform Stubs (`src/jvmMain/kotlin/...`)**:
  - Implement ONLY the minimal stubs required for UI compilation (e.g., `Platform.jvm.kt` returning `DESKTOP`).
  - No-op stubs for all hardware/media/DB/network logic.

### 4. Implement `Playground` Shell
- **`Playground/build.gradle.kts`**:
  - Apply `kotlin("multiplatform")`, `jetbrains.compose`, `compose.compiler`.
  - Depend on `project(":muse")`.
  - Configure `compose.desktop.application`.
- **`Playground/src/jvmMain/kotlin/Main.kt`**:
  - A simple `androidx.compose.ui.window.Window` entry point.
  - Use it to host and test individual UI components from `muse`.

## Verification
- Run `./gradlew :Playground:run` to verify UI rendering of individual components.
- Ensure Android and iOS builds are unaffected.
