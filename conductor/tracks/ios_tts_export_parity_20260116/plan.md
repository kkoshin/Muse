# Implementation Plan - iOS TTS Export Completion

This plan outlines the steps to complete the iOS implementation for TTS audio export, achieving parity with the Android implementation.

## Phase 1: Infrastructure and Configuration [checkpoint: b85cde5]
Establish the necessary iOS-specific configurations and implement the missing MP3 encoding logic.

- [x] Task: Update iOS `Info.plist` for Files app integration (152092c)
    - [x] Add `UIFileSharingEnabled` (Application supports iTunes file sharing) set to `YES`.
    - [x] Add `LSSupportsOpeningDocumentsInPlace` (Supports opening documents in place) set to `YES`.
- [x] Task: Implement `Mp3Encoder` for iOS (7a5abd9)
    - [x] Implement `Mp3Encoder.ios.kt` using the `lame` CocoaPods dependency.
    - [x] Implement `writeMonoAudio` and `writeStereoAudio` logic similar to Android.
    - [x] Ensure proper resource management (closing LAME handle).
- [x] Task: Implement `Mp3Decoder` for iOS (9002017)
    - [x] Implement `Mp3Decoder.ios.kt` using `AVAssetReader` to decode MP3 to PCM.
    - [x] Implement volume boost logic consistent with Android.
- [x] Task: Conductor - User Manual Verification 'Phase 1: Infrastructure and Configuration' (Protocol in workflow.md) (b85cde5)

## Phase 2: Platform Bridge Implementation [checkpoint: b32d1a0]
Implement the platform-specific logic for saving files to the Documents directory on iOS.

- [x] Task: Implement `MediaStoreHelper.exportFileToDownload` for iOS (ac6b467)
    - [x] Resolve the `Documents` directory path using `NSFileManager`.
    - [x] Create the `Exports` sub-directory if it doesn't exist.
    - [x] Return the `okio.Path` for the target file.
- [x] Task: Add Share Sheet Trigger in `PlatformInfo` or new bridge (Already implemented in `FileUtils.kt`)
    - [x] Define an `expect` function or interface in `commonMain` for sharing a file.
    - [x] Implement the `actual` function for iOS using `UIActivityViewController`.
    - [x] Implement the `actual` function for Android (as a placeholder if not already existing, or use existing `Intent` logic).
- [x] Task: Conductor - User Manual Verification 'Phase 2: Platform Bridge Implementation' (Protocol in workflow.md) (b32d1a0)

## Phase 3: UI Integration and Testing
Connect the platform-specific logic to the shared UI and verify the end-to-end flow.

- [x] Task: Update `ExportScreen` and `ExportViewModel` (Existing common UI already integrated)
    - [x] Ensure the "Share" button is visible and functional on iOS after a successful export.
    - [x] Verify that the `AudioExportPipeline` correctly uses the new `Mp3Encoder` on iOS.
- [x] Task: End-to-End Verification (927946a)
- [ ] Task: Conductor - User Manual Verification 'Phase 3: UI Integration and Testing' (Protocol in workflow.md)
