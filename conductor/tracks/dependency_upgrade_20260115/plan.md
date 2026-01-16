# Plan: Dependency Upgrade (Core & Build Tools)

## Phase 1: Preparation and Baseline [checkpoint: 7a7a4c5]
- [x] Task: Document current dependency versions and verify baseline build status. f5fb393
- [x] Task: Conductor - User Manual Verification 'Phase 1: Preparation and Baseline' (Protocol in workflow.md) 7a7a4c5

## Phase 2: Core Build Tools Upgrade [checkpoint: ]
- [x] Task: Update Android Gradle Plugin (AGP) and Kotlin Gradle Plugin (KGP) in `gradle/libs.versions.toml`.
- [x] Task: Update Compose Multiplatform compiler and plugin versions.
- [x] Task: Sync Gradle and resolve any build script errors.
- [ ] Task: Conductor - User Manual Verification 'Phase 2: Core Build Tools Upgrade' (Protocol in workflow.md)

## Phase 3: Kotlin Multiplatform & Compose Libraries Upgrade [checkpoint: ]
- [ ] Task: Update Kotlin Multiplatform core library versions (stdlib, coroutines, etc. if explicitly versioned).
- [ ] Task: Update Compose Multiplatform UI library versions.
- [ ] Task: Run Android build and fix any compilation errors/breaking changes.
- [ ] Task: Conductor - User Manual Verification 'Phase 3: Kotlin Multiplatform & Compose Libraries Upgrade' (Protocol in workflow.md)

## Phase 4: iOS Build and Launch Verification [checkpoint: ]
- [ ] Task: Verify shared module build for iOS target (`./gradlew :muse:assembleXCFramework`).
- [ ] Task: Open `swiftApp` in Xcode or use CLI to build the iOS application.
- [ ] Task: Resolve any iOS-specific build errors (e.g., CocoaPods issues, Linker errors).
- [ ] Task: Launch the iOS application in a simulator and verify basic functionality (one-tap TTS).
- [ ] Task: Conductor - User Manual Verification 'Phase 4: iOS Build and Launch Verification' (Protocol in workflow.md)

## Phase 5: Finalization and Cleanup [checkpoint: ]
- [ ] Task: Perform a final clean build of both Android and iOS applications.
- [ ] Task: Verify that `gradle/libs.versions.toml` only contains stable versions.
- [ ] Task: Conductor - User Manual Verification 'Phase 5: Finalization and Cleanup' (Protocol in workflow.md)
