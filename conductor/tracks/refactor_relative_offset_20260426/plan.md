# Implementation Plan: Refactor CaptionView Transform to RelativeOffset

## Phase 1: Foundation
- [ ] Task: Update `CaptionTransform` data class to use `x: Float, y: Float` (normalized) instead of `DpOffset`.
- [ ] Task: Implement extension functions/helpers for mapping between Relative and Pixel coordinates.
- [ ] Task: Conductor - User Manual Verification 'Foundation' (Protocol in workflow.md)

## Phase 2: Integration
- [ ] Task: Update `CaptionRender.kt` to use relative positioning.
- [ ] Task: Update `CaptionView.kt` drag logic to handle normalized coordinates.
- [ ] Task: Update `ExportManager.kt` to interpret relative offsets for target resolution.
- [ ] Task: Conductor - User Manual Verification 'Integration' (Protocol in workflow.md)

## Phase 3: Validation & Cleanup
- [ ] Task: Verify that resizing the Playground window preserves caption position.
- [ ] Task: Verify that exported images are correctly formatted.
- [ ] Task: Remove any remaining `DpOffset` logic related to caption transformation.
- [ ] Task: Conductor - User Manual Verification 'Validation & Cleanup' (Protocol in workflow.md)
