# Specification - iOS TTS Export Completion

## Overview
Currently, the Muse app can export TTS audio to MP3 on Android, but the implementation is incomplete on iOS. This track aims to achieve parity by implementing the MP3 encoding logic using the LAME library and providing a seamless way for iOS users to access and share their exported files.

## Functional Requirements
- **MP3 Encoding:** Implement the `Mp3Encoder` for iOS using the LAME library (already included as a CocoaPods dependency).
- **File Export:** Implement `MediaStoreHelper.exportFileToDownload` for iOS to save the encoded MP3 file to the app's `Documents` directory.
- **Files App Integration:** Enable "File Sharing" and "Open Documents in Place" in the iOS `Info.plist` to make exported files visible in the system "Files" app.
- **Share/Open Functionality:** Provide a mechanism (triggered by user action on the completion screen) to open the system Share sheet for the exported file.

## Non-Functional Requirements
- **Consistency:** The iOS implementation should follow the patterns established in the Android implementation where applicable (e.g., pipeline structure, metadata handling).
- **Performance:** Encoding should be performed asynchronously to avoid blocking the UI thread.

## Acceptance Criteria
- [ ] `Mp3Encoder` successfully converts WAV/PCM data to MP3 on iOS.
- [ ] Exported MP3 files are saved to the iOS `Documents/Exports` directory.
- [ ] Exported files are visible and accessible within the iOS "Files" app under the "Muse" folder (or app name).
- [ ] After export, the user can successfully trigger the iOS Share sheet to send the file to other apps or save it elsewhere.
- [ ] The export process shows progress and handles errors gracefully, consistent with the Android experience.

## Out of Scope
- Implementation of video export on iOS (this track focuses on audio/MP3 parity).
- Changes to the core TTS generation logic (ElevenLabs integration).
