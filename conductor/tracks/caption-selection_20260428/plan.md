# Implementation Plan: Caption Selection Logic Refinement

## Phase 1: Test Suite Update (Red Phase) [checkpoint: d10c495]
- [x] Task: Locate the existing test file for `CaptionView` or create a new one (e.g., `CaptionViewTest.kt` in the appropriate source set, likely `desktopTest` or `commonTest` depending on the architecture).
- [x] Task: Write tests to verify clicking inside caption bounds selects it.
- [x] Task: Write tests to verify clicking outside caption bounds (including outside the padding) does NOT select it.
- [x] Task: Write tests to verify clicking within the extended padded hit area selects the caption.
- [x] Task: Write tests to verify overlapping caption selection prioritizes the topmost element.
- [x] Task: Run tests to ensure they fail (Red Phase).
- [x] Task: Conductor - User Manual Verification 'Phase 1: Test Suite Update (Red Phase)' (Protocol in workflow.md)

## Phase 2: Implementation (Green Phase) [checkpoint: d10c495]
- [x] Task: Modify `CaptionView.kt` pointer input/click logic.
    - [x] Update the click modifier to use exact coordinates instead of consuming clicks globally.
    - [x] Calculate visual bounds of the caption.
    - [x] Add the padding threshold to the hit detection logic.
    - [x] Update the state selection to only trigger if the click falls within these augmented bounds.
- [x] Task: Ensure Compose Multiplatform Z-order naturally handles topmost clicks, or explicitly implement Z-order hit testing if custom canvas drawing is used.
- [x] Task: Run tests to ensure they pass (Green Phase).
- [x] Task: Conductor - User Manual Verification 'Phase 2: Implementation (Green Phase)' (Protocol in workflow.md)

## Phase 3: Refactoring and Manual Verification [checkpoint: 1a76050]
- [x] Task: Refactor any messy coordinate calculations in `CaptionView.kt` for better readability.
- [x] Task: Verify the changes manually in the Playground app (run Desktop app, click around captions).
- [x] Task: Conductor - User Manual Verification 'Phase 3: Refactoring and Manual Verification' (Protocol in workflow.md)