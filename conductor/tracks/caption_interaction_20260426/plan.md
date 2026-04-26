# Implementation Plan: CaptionView Interactive Selection and Transformation

## Phase 1: Foundation & Selection
- [x] Task: Update `CaptionTransform` data class to include `scale: Float`.
- [x] Task: Refactor `CaptionView` state to include `isSelectionBoxVisible: Boolean`.
- [x] Task: Implement `pointerInput` on the `Canvas` to detect clicks and toggle `isSelectionBoxVisible`.
- [x] Task: Update `SelectionBox` to dynamically calculate its size based on the measured text bounding box.
- [x] Task: Connect the "Close" button in `SelectionBox` to hide the box.
- [ ] Task: Conductor - User Manual Verification 'Foundation & Selection' (Protocol in workflow.md)

## Phase 2: Movement Interaction (Drag)
- [x] Task: Implement `detectDragGestures` on the `SelectionBox` to update `captionTransform.offset`.
- [x] Task: Ensure real-time update of the text position on the canvas during drag.
- [ ] Task: Conductor - User Manual Verification 'Movement Interaction' (Protocol in workflow.md)

## Phase 3: Scaling Interaction (Zoom)
- [x] Task: Implement `detectDragGestures` on the "Scale" handle button.
- [x] Task: Calculate new scale based on drag distance from center and update `captionTransform.scale`.
- [x] Task: Ensure real-time update of the text scale on the canvas during drag.
- [ ] Task: Conductor - User Manual Verification 'Scaling Interaction' (Protocol in workflow.md)