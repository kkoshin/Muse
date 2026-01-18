# Implementation Plan - iOS Audio Isolation Parity

This plan outlines the steps to move the Audio Isolation feature to shared code and enable it for iOS.

## Phase 1: Shared Core Refactoring
Refactor the ViewModel and supporting logic to be platform-agnostic in `commonMain`.

- [x] Task: Refactor `AudioIsolationViewModel` (cdd1418) to use `okio.Path` instead of `android.net.Uri`
    - [ ] Create unit tests in `commonTest` for `AudioIsolationViewModel` behavior.
    - [ ] Remove `android.content.Context` and `android.net.Uri` dependencies.
    - [ ] Move `AudioIsolationViewModel` from `androidMain` to `commonMain`.
- [ ] Task: Move `AudioIsolation` UI components to `commonMain`
    - [ ] Move `AudioIsolationScreen.kt` and `AudioIsolationPreviewScreen.kt` to `commonMain`.
    - [ ] Update imports and resource references to use shared `Res`.
- [ ] Task: Conductor - User Manual Verification 'Shared Core Refactoring' (Protocol in workflow.md)

## Phase 2: Navigation and Platform Integration
Integrate the shared UI into the iOS application and update navigation.

- [ ] Task: Update Shared Navigation in `MainScreen.kt`
    - [ ] Define `AudioIsolationArgs` and `AudioIsolationPreviewArgs` in shared code if not already.
    - [ ] Add `composable` and `bottomSheet` routes for Audio Isolation in the shared `NavHost`.
- [ ] Task: Register Dependencies for iOS
    - [ ] Add `AudioIsolationViewModel` to the Koin `appModule` in `iosMain`.
- [ ] Task: Implement `onLaunchAudioIsolation` for iOS
    - [ ] Update `MainScreen.ios.kt` to trigger navigation to the newly shared routes.
- [ ] Task: Conductor - User Manual Verification 'Navigation and Platform Integration' (Protocol in workflow.md)

## Phase 3: Verification and Finalization
Ensure parity and quality across both platforms.

- [ ] Task: Verify Android Parity
    - [ ] Run the app on Android and ensure Audio Isolation still works as expected.
- [ ] Task: Verify iOS Implementation
    - [ ] Run the app on iOS and verify the end-to-end Audio Isolation flow.
- [ ] Task: Conductor - User Manual Verification 'Verification and Finalization' (Protocol in workflow.md)
