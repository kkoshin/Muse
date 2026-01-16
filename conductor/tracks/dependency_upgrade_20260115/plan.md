# Plan: Dependency Upgrade (Core & Build Tools)

## Phase 1: Preparation and Baseline [checkpoint: 7a7a4c5]
- [x] Task: Document current dependency versions and verify baseline build status. f5fb393
- [x] Task: Conductor - User Manual Verification 'Phase 1: Preparation and Baseline' (Protocol in workflow.md) 7a7a4c5

## Phase 2: Core Build Tools Upgrade [checkpoint: 4b16abb]
- [x] Task: Update Android Gradle Plugin (AGP) and Kotlin Gradle Plugin (KGP) in `gradle/libs.versions.toml`.
- [x] Task: Update Compose Multiplatform compiler and plugin versions.
- [x] Task: Sync Gradle and resolve any build script errors.
- [x] Task: Conductor - User Manual Verification 'Phase 2: Core Build Tools Upgrade' (Protocol in workflow.md) 4b16abb

## Phase 3: Kotlin Multiplatform & Compose Libraries Upgrade [checkpoint: 3d509b3]
- [x] Task: Update Kotlin Multiplatform core library versions (stdlib, coroutines, etc. if explicitly versioned). 830fd4b
- [x] Task: Update Compose Multiplatform UI library versions. 830fd4b
- [x] Task: Run Android build and fix any compilation errors/breaking changes. 830fd4b
- [x] Task: Conductor - User Manual Verification 'Phase 3: Kotlin Multiplatform & Compose Libraries Upgrade' (Protocol in workflow.md) 3d509b3

## Phase 4: iOS Build and Launch Verification [checkpoint: f23ea8e]
- [x] Task: Verify shared module build for iOS target (`./gradlew :muse:assembleXCFramework`). 331100a
- [x] Task: Open `swiftApp` in Xcode or use CLI to build the iOS application. 331100a
- [x] Task: Resolve any iOS-specific build errors (e.g., CocoaPods issues, Linker errors). 331100a
- [x] Task: Launch the iOS application in a simulator and verify basic functionality (one-tap TTS). 331100a
- [x] Task: Conductor - User Manual Verification 'Phase 4: iOS Build and Launch Verification' (Protocol in workflow.md) f23ea8e

## Phase 5: Finalization and Cleanup [checkpoint: 14c74b3]
- [x] Task: Perform a final clean build of both Android and iOS applications.
- [x] Task: Verify that `gradle/libs.versions.toml` only contains stable versions.
- [x] Task: Conductor - User Manual Verification 'Phase 5: Finalization and Cleanup' (Protocol in workflow.md) 14c74b3
