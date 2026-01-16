# Implementation Plan - iOS TTS Export Completion

This plan outlines the steps to complete the iOS implementation for TTS audio export, achieving parity with the Android implementation.

## Phase 1: Infrastructure and Configuration
Establish the necessary iOS-specific configurations and implement the missing MP3 encoding logic.

- [x] Task: Update iOS `Info.plist` for Files app integration (152092c)
    - [x] Add `UIFileSharingEnabled` (Application supports iTunes file sharing) set to `YES`.
    - [x] Add `LSSupportsOpeningDocumentsInPlace` (Supports opening documents in place) set to `YES`.
- [x] Task: Implement `Mp3Encoder` for iOS (7a5abd9)
    - [x] Implement `Mp3Encoder.ios.kt` using the `lame` CocoaPods dependency.
    - [x] Implement `writeMonoAudio` and `writeStereoAudio` logic similar to Android.
    - [x] Ensure proper resource management (closing LAME handle).
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Infrastructure and Configuration' (Protocol in workflow.md)

## Phase 2: Platform Bridge Implementation
Implement the platform-specific logic for saving files to the Documents directory on iOS.

- [ ] Task: Implement `MediaStoreHelper.exportFileToDownload` for iOS
    - [ ] Resolve the `Documents` directory path using `NSFileManager`.
    - [ ] Create the `Exports` sub-directory if it doesn't exist.
    - [ ] Return the `okio.Path` for the target file.
- [ ] Task: Add Share Sheet Trigger in `PlatformInfo` or new bridge
    - [ ] Define an `expect` function or interface in `commonMain` for sharing a file.
    - [ ] Implement the `actual` function for iOS using `UIActivityViewController`.
    - [ ] Implement the `actual` function for Android (as a placeholder if not already existing, or use existing `Intent` logic).
- [ ] Task: Conductor - User Manual Verification 'Phase 2: Platform Bridge Implementation' (Protocol in workflow.md)

## Phase 3: UI Integration and Testing
Connect the platform-specific logic to the shared UI and verify the end-to-end flow.

- [ ] Task: Update `ExportScreen` and `ExportViewModel`
    - [ ] Ensure the "Share" button is visible and functional on iOS after a successful export.
    - [ ] Verify that the `AudioExportPipeline` correctly uses the new `Mp3Encoder` on iOS.
- [ ] Task: End-to-End Verification
    - [ ] Run the app on an iOS simulator or device.
    - [ ] Perform a TTS export and verify the progress UI.
    - [ ] Confirm the file is saved in the "Files" app under "On My iPhone/Muse/Exports".
    - [ ] Verify the "Share" button opens the iOS Share sheet and functions correctly.
- [ ] Task: Conductor - User Manual Verification 'Phase 3: UI Integration and Testing' (Protocol in workflow.md)
