/# Implementation Plan: Refactor CaptionView Transform to RelativeOffset

## Phase 1: Foundation
- [x] Task: Update `CaptionTransform` data class to use `x: Float, y: Float` (normalized) instead of `DpOffset`.
- [x] Task: Implement extension functions/helpers for mapping between Relative and Pixel coordinates.
- [x] Task: Conductor - User Manual Verification 'Foundation' (Protocol in workflow.md)

## Phase 2: Integration
- [x] Task: Update `CaptionRender.kt` to use relative positioning.
- [x] Task: Update `CaptionView.kt` drag logic to handle normalized coordinates.
- [x] Task: Update `ExportManager.kt` to interpret relative offsets for target resolution. (Handled in CaptionRender)
- [x] Task: Conductor - User Manual Verification 'Integration' (Protocol in workflow.md)

## Phase 3: Validation & Cleanup
- [x] Task: Verify that resizing the Playground window preserves caption position.
- [x] Task: Verify that exported images are correctly formatted.
- [x] Task: Remove any remaining `DpOffset` logic related to caption transformation.
- [ ] Task: Conductor - User Manual Verification 'Validation & Cleanup' (Protocol in workflow.md)
