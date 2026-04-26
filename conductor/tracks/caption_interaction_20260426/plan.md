# Implementation Plan: CaptionView Interactive Selection and Transformation

## Phase 1: Foundation & Selection
- [ ] Task: Update `CaptionTransform` data class to include `scale: Float`.
- [ ] Task: Refactor `CaptionView` state to include `isSelectionBoxVisible: Boolean`.
- [ ] Task: Implement `pointerInput` on the `Canvas` to detect clicks and toggle `isSelectionBoxVisible`.
- [ ] Task: Update `SelectionBox` to dynamically calculate its size based on the measured text bounding box.
- [ ] Task: Connect the "Close" button in `SelectionBox` to hide the box.
- [ ] Task: Conductor - User Manual Verification 'Foundation & Selection' (Protocol in workflow.md)

## Phase 2: Movement Interaction (Drag)
- [ ] Task: Write unit tests for offset calculation logic (if logic is extracted).
- [ ] Task: Implement `detectDragGestures` on the `SelectionBox` to update `captionTransform.offset`.
- [ ] Task: Ensure real-time update of the text position on the canvas during drag.
- [ ] Task: Conductor - User Manual Verification 'Movement Interaction' (Protocol in workflow.md)

## Phase 3: Scaling Interaction (Zoom)
- [ ] Task: Write unit tests for scale calculation logic relative to the box center.
- [ ] Task: Implement `detectDragGestures` on the "Scale" handle button.
- [ ] Task: Calculate new scale based on drag distance from center and update `captionTransform.scale`.
- [ ] Task: Ensure real-time update of the text scale on the canvas during drag.
- [ ] Task: Conductor - User Manual Verification 'Scaling Interaction' (Protocol in workflow.md)