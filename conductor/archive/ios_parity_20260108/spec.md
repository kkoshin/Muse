# Track Spec: iOS Parity & ElevenLabs Integration

## 1. Goal
Achieve feature parity between the iOS and Android versions of Muse. This involves completing the ElevenLabs integration for iOS and ensuring the Compose Multiplatform UI works seamlessly on iOS, replacing any placeholder or missing functionality.

## 2. Core Requirements
- **ElevenLabs Integration (iOS):**
    - Implement network calls to ElevenLabs API for iOS (reusing common Ktor logic if possible).
    - Handle audio file downloads and local storage on iOS.
    - Implement audio playback using Native Darwin APIs (or Media3 via KMP wrapper if feasible/preferred) for the generated content.
- **UI Parity:**
    - Reuse existing Compose Multiplatform UI components for the iOS target.
    - Ensure all screens (Text Input, History/Library, Settings) function correctly on iOS.
    - **Constraint:** NO SwiftUI or UIKit for UI implementation. Pure Compose Multiplatform.
- **Data Persistence:**
    - Ensure SQLDelight database works correctly on iOS for saving history.
    - Ensure Settings (DataStore/Preferences) are synced and functional.

## 3. Tasks Breakdown
- **Phase 1: Architecture & Data Layer (iOS)**
    - Verify/Configure SQLDelight Native Driver for iOS.
    - Verify/Configure Ktor Client for iOS (Darwin engine).
    - Implement/Verify File System access for saving audio files on iOS.
- **Phase 2: ElevenLabs Integration (iOS)**
    - Implement the repository logic for fetching TTS audio on iOS (if platform specific logic is needed beyond common code).
    - Implement audio playback mechanism for iOS (using AVPlayer via KMP expect/actual or a KMP library).
- **Phase 3: UI Integration & Verification**
    - Connect the shared Compose UI to the iOS `MainViewController`.
    - Verify all screens and interactions on iOS.
    - Polish UI layout differences if necessary (safe area handling, etc.).

## 4. Success Criteria
- [ ] User can input text on iOS and generate speech via ElevenLabs.
- [ ] Generated audio plays back correctly on the device.
- [ ] Generated audio and metadata are saved to the history (database) and persisted across app restarts.
- [ ] UI looks and feels consistent with the Android version (Material 3).
- [ ] >80% code coverage for new iOS-specific logic (where testable).
