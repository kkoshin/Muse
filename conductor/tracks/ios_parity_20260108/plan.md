# Track Plan: iOS Parity & ElevenLabs Integration

## Phase 1: Architecture & Data Layer (iOS)
- [x] Task: Configure SQLDelight Native Driver for iOS 48d29fe
    - [x] Subtask: Write Tests (Verify database creation and table access on iOS)
    - [x] Subtask: Implement Feature (Add/Configure `NativeSqliteDriver` in `iosMain`)
- [ ] Task: Configure Ktor Client for iOS
    - [ ] Subtask: Write Tests (Verify HTTP client instantiation and basic connectivity in `iosTest`)
    - [ ] Subtask: Implement Feature (Add/Configure `Darwin` engine in `iosMain`)
- [ ] Task: Implement File System Access for iOS
    - [ ] Subtask: Write Tests (Verify file writing and reading in `iosTest`)
    - [ ] Subtask: Implement Feature (Implement expect/actual for file storage path and IO operations)
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Architecture & Data Layer (iOS)' (Protocol in workflow.md)

## Phase 2: ElevenLabs Integration (iOS)
- [ ] Task: Implement Audio Playback for iOS
    - [ ] Subtask: Write Tests (Mock AudioPlayer interface and verify interaction)
    - [ ] Subtask: Implement Feature (Implement `AudioPlayer` expect/actual using `AVPlayer` for iOS)
- [ ] Task: Verify ElevenLabs Repository on iOS
    - [ ] Subtask: Write Tests (Integration test with mocked Ktor engine to verify API handling on iOS target)
    - [ ] Subtask: Implement Feature (Ensure shared repository logic functions correctly with iOS dependencies)
- [ ] Task: Conductor - User Manual Verification 'Phase 2: ElevenLabs Integration (iOS)' (Protocol in workflow.md)

## Phase 3: UI Integration - Core & Input
- [ ] Task: Connect Shared UI to iOS MainViewController
    - [ ] Subtask: Write Tests (UI Tests for iOS target are limited, focus on common UI logic tests)
    - [ ] Subtask: Implement Feature (Setup `ComposeUIViewController` in `iosMain` and wire to `RootContent`)
- [ ] Task: Verify and Polish Input Screen (iOS)
    - [ ] Subtask: Write Tests (Verify InputViewModel state transitions on iOS target)
    - [ ] Subtask: Implement Feature (Fix text field interactions, focus handling, and generation triggers on iOS)
- [ ] Task: Conductor - User Manual Verification 'Phase 3: UI Integration - Core & Input' (Protocol in workflow.md)

## Phase 4: UI Integration - History & Settings
- [ ] Task: Verify and Polish History Screen (iOS)
    - [ ] Subtask: Write Tests (Verify HistoryViewModel list loading on iOS)
    - [ ] Subtask: Implement Feature (Fix lazy list scrolling, item clicks, and playback controls on iOS)
- [ ] Task: Verify and Polish Settings Screen (iOS)
    - [ ] Subtask: Write Tests (Verify SettingsViewModel preference updates on iOS)
    - [ ] Subtask: Implement Feature (Ensure toggles/dropdowns work and save to DataStore correctly on iOS)
- [ ] Task: Handle Safe Areas and Global Platform Specifics
    - [ ] Subtask: Write Tests (Visual inspection required)
    - [ ] Subtask: Implement Feature (Apply `WindowInsets` and platform adjustments in Compose)
- [ ] Task: Conductor - User Manual Verification 'Phase 4: UI Integration - History & Settings' (Protocol in workflow.md)