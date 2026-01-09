# Track Plan: iOS Parity & ElevenLabs Integration

## Phase 1: Architecture & Data Layer (iOS) [checkpoint: d96fc24]
- [x] Task: Configure SQLDelight Native Driver for iOS 48d29fe
    - [x] Subtask: Write Tests (Verify database creation and table access on iOS)
    - [x] Subtask: Implement Feature (Add/Configure `NativeSqliteDriver` in `iosMain`)
- [x] Task: Configure Ktor Client for iOS 01488ec
    - [x] Subtask: Write Tests (Verify HTTP client instantiation and basic connectivity in `iosTest`)
    - [x] Subtask: Implement Feature (Add/Configure `Darwin` engine in `iosMain`)
- [x] Task: Implement File System Access for iOS 4fa084c
    - [x] Subtask: Write Tests (Verify file writing and reading in `iosTest`)
    - [x] Subtask: Implement Feature (Implement expect/actual for file storage path and IO operations)
- [x] Task: Conductor - User Manual Verification 'Phase 1: Architecture & Data Layer (iOS)' (Protocol in workflow.md) d96fc24

## Phase 2: ElevenLabs Integration (iOS) [checkpoint: 17e3cad]
- [x] Task: Implement Audio Playback for iOS aa17ef1
    - [x] Subtask: Write Tests (Mock AudioPlayer interface and verify interaction)
    - [x] Subtask: Implement Feature (Implement `AudioPlayer` expect/actual using `AVPlayer` for iOS)
- [x] Task: Verify ElevenLabs Repository on iOS 6225e2e
    - [x] Subtask: Write Tests (Integration test with mocked Ktor engine to verify API handling on iOS target)
    - [x] Subtask: Implement Feature (Ensure shared repository logic functions correctly with iOS dependencies)
- [x] Task: Conductor - User Manual Verification 'Phase 2: ElevenLabs Integration (iOS)' (Protocol in workflow.md) 17e3cad

## Phase 3: UI Integration - Core & Input [checkpoint: f20edf7]
- [x] Task: Connect Shared UI to iOS MainViewController 7a0c879
    - [x] Subtask: Write Tests (UI Tests for iOS target are limited, focus on common UI logic tests)
    - [x] Subtask: Implement Feature (Setup `ComposeUIViewController` in `iosMain` and wire to `RootContent`)
- [x] Task: Verify and Polish Input Screen (iOS) f3542d1
    - [x] Subtask: Write Tests (Verify InputViewModel state transitions on iOS target)
    - [x] Subtask: Implement Feature (Fix text field interactions, focus handling, and generation triggers on iOS)
- [x] Task: Conductor - User Manual Verification 'Phase 3: UI Integration - Core & Input' (Protocol in workflow.md) f20edf7

## Phase 4: UI Integration - History & Settings
- [x] Task: Verify and Polish History Screen (iOS) c6d0c4c
    - [x] Subtask: Write Tests (Verify HistoryViewModel list loading on iOS)
    - [x] Subtask: Implement Feature (Fix lazy list scrolling, item clicks, and playback controls on iOS)
- [x] Task: Verify and Polish Settings Screen (iOS) 97d5bbc
    - [x] Subtask: Write Tests (Verify SettingsViewModel preference updates on iOS)
    - [x] Subtask: Implement Feature (Ensure toggles/dropdowns work and save to DataStore correctly on iOS)
- [x] Task: Handle Safe Areas and Global Platform Specifics d7ef9bb
    - [x] Subtask: Write Tests (Visual inspection required)
    - [x] Subtask: Implement Feature (Apply `WindowInsets` and platform adjustments in Compose)
- [ ] Task: Conductor - User Manual Verification 'Phase 4: UI Integration - History & Settings' (Protocol in workflow.md)