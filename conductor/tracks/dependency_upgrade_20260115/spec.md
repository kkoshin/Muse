# Specification: Dependency Upgrade (Core & Build Tools)

## Overview
Upgrade the project's core dependencies—specifically Kotlin Multiplatform (KMP), Compose Multiplatform, and primary build tools (Android Gradle Plugin, Kotlin Gradle Plugin)—to their latest stable versions. The focus is on maintaining project health and ensuring the iOS application remains buildable and launchable.

## Functional Requirements
- **Dependency Update**: Update versions in `gradle/libs.versions.toml` for Kotlin, Compose Multiplatform, and Gradle plugins (AGP, etc.).
- **Breaking Changes**: Resolve any immediate API breaking changes or deprecations resulting from the upgrades.
- **iOS Build Verification**: Ensure the `swiftApp` project and the shared `composeApp` module build successfully for the iOS target.
- **Basic Functional Test**: Verify the iOS application launches successfully in a simulator.

## Non-Functional Requirements
- **Stability**: Only upgrade to "Stable" versions. Avoid Alpha, Beta, or Release Candidate (RC) versions.
- **Minimal Impact**: Avoid updating the Gradle Wrapper unless it is strictly required by the new plugin versions.
- **Complexity Management**: If an upgrade introduces highly complex breaking changes requiring extensive refactoring, that specific upgrade will be deferred to a separate track.

## Acceptance Criteria
- `gradle/libs.versions.toml` is updated with latest stable versions for KMP, Compose, and Build Plugins.
- The project successfully syncs with Gradle.
- Android application builds and runs.
- iOS application (`swiftApp`) builds successfully in Xcode/via CLI.
- iOS application successfully launches in the iOS Simulator.

## Out of Scope
- Global upgrade of all third-party libraries (Koin, Ktor, etc.) unless they must be updated to support the new Kotlin/Compose versions.
- Large-scale architectural refactoring.
