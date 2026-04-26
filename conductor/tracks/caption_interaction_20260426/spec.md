# Specification: CaptionView Interactive Selection and Transformation

## Overview
Enhance the `CaptionView` in the `Playground` desktop module to support interactive selection, movement, and scaling of the rendered caption. Users should be able to click on the canvas to activate a `SelectionBox` that matches the text's bounding box, then manipulate the text via dragging and scaling.

## Functional Requirements
- **Selection Interaction:**
  - Clicking anywhere on the canvas (or specifically on the text) should make the `SelectionBox` visible.
  - The `SelectionBox` must automatically resize to match the current visual bounding box of the text.
- **Deselection Interaction:**
  - Clicking the "Close" button on the `SelectionBox` must hide it.
- **Movement (Drag):**
  - While the `SelectionBox` is visible, dragging the box area should move the text preview in real-time.
  - The movement must be "silky smooth" and follow the mouse pointer precisely.
- **Scaling (Zoom):**
  - Dragging the "Scale" (Expand icon) button at the bottom-right of the `SelectionBox` should scale the text.
  - **Pivot Point:** Scaling must be relative to the **center** of the text box.
  - **Scale Limits:** No predefined limits (Free Scaling).
  - The scaling interaction must be fluid, with the text size updating continuously during the drag.

## Non-Functional Requirements
- **Performance:** Ensure high-performance rendering (60+ FPS) during drag and scale operations.
- **UX:** Provide intuitive visual feedback (e.g., cursor changes) during interaction.

## Acceptance Criteria
- `SelectionBox` appears on click and matches text size.
- Clicking the close button hides the `SelectionBox`.
- Dragging the `SelectionBox` moves the text smoothly.
- Dragging the scale handle resizes the text smoothly relative to its center.

## Out of Scope
- Rotation of the text.
- Multi-touch gestures (focused on desktop mouse interaction).
- Persisting the transform state across sessions (for this specific track).