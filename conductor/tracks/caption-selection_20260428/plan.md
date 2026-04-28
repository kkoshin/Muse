# Implementation Plan: Caption Selection Logic Refinement

## Phase 1: Test Suite Update (Red Phase)
- [ ] Task: Locate the existing test file for `CaptionView` or create a new one (e.g., `CaptionViewTest.kt` in the appropriate source set, likely `desktopTest` or `commonTest` depending on the architecture).
- [ ] Task: Write tests to verify clicking inside caption bounds selects it.
- [ ] Task: Write tests to verify clicking outside caption bounds (including outside the padding) does NOT select it.
- [ ] Task: Write tests to verify clicking within the extended padded hit area selects the caption.
- [ ] Task: Write tests to verify overlapping caption selection prioritizes the topmost element.
- [ ] Task: Run tests to ensure they fail (Red Phase).
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Test Suite Update (Red Phase)' (Protocol in workflow.md)

## Phase 2: Implementation (Green Phase)
- [ ] Task: Modify `CaptionView.kt` pointer input/click logic.
    - [ ] Update the click modifier to use exact coordinates instead of consuming clicks globally.
    - [ ] Calculate visual bounds of the caption.
    - [ ] Add the padding threshold to the hit detection logic.
    - [ ] Update the state selection to only trigger if the click falls within these augmented bounds.
- [ ] Task: Ensure Compose Multiplatform Z-order naturally handles topmost clicks, or explicitly implement Z-order hit testing if custom canvas drawing is used.
- [ ] Task: Run tests to ensure they pass (Green Phase).
- [ ] Task: Conductor - User Manual Verification 'Phase 2: Implementation (Green Phase)' (Protocol in workflow.md)

## Phase 3: Refactoring and Manual Verification
- [ ] Task: Refactor any messy coordinate calculations in `CaptionView.kt` for better readability.
- [ ] Task: Verify the changes manually in the Playground app (run Desktop app, click around captions).
- [ ] Task: Conductor - User Manual Verification 'Phase 3: Refactoring and Manual Verification' (Protocol in workflow.md)