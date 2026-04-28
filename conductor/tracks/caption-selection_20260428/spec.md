# Specification: Caption Selection Logic Refinement

## Overview
Currently, clicking anywhere on the screen in `CaptionView.kt` selects the corresponding Caption. This track aims to modify the selection logic so that the Caption is only selected when the user actually clicks within its visual bounds (plus a small padding for better usability).

## Functional Requirements
- **Hit Detection:** The `CaptionView` must only register a selection if the tap/click coordinates fall within the calculated bounds of the caption text/container.
- **Touch Target Expansion:** The clickable area must include a small, defined padding around the exact visual bounds to improve tap accuracy, especially on smaller screens or touch devices.
- **Z-Order Respect (Overlapping):** If multiple captions overlap at the location of the click, only the topmost caption (the one highest in the Z-index) should be selected.
- **Deselection:** Clicking outside the bounds of any caption should clear the current selection (if applicable, to maintain consistency with the new precise clicking logic).

## Out of Scope
- Major restructuring of the `CaptionView` layout engine.
- Adding new visual styling for the selected state (only modifying *how* the selection is triggered).

## Acceptance Criteria
- Clicking exactly on a caption selects it.
- Clicking slightly outside (within the padding zone) of a caption selects it.
- Clicking far outside a caption does not select it.
- In a scenario where two captions overlap, clicking the overlapping region selects the top one.