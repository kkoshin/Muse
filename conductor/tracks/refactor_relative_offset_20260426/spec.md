# Specification: Refactor CaptionView Transform to RelativeOffset

## Overview
Refactor the transformation implementation in `CaptionView` from fixed `DpOffset` to a normalized `RelativeOffset` (0.0 to 1.0). This ensures that captions maintain their proportional position regardless of the canvas size or the resolution used for exporting.

## Functional Requirements
- **Relative Coordinate System**:
    - Use a normalized range (0.0 to 1.0) for both x and y coordinates.
    - (0.0, 0.0) is the top-left of the container.
    - (0.5, 0.5) is the center (default position).
    - (1.0, 1.0) is the bottom-right.
- **Dynamic Positioning**:
    - Rendering in `CaptionView` must calculate pixel positions dynamically based on current `Canvas` size.
    - Captions must stay at the same proportional position when the window/canvas is resized.
- **Interaction Mapping**:
    - Update drag/transform logic in `CaptionView` to map pixel displacements back to relative coordinate changes.
- **Export Consistency**:
    - Update `ExportManager` to correctly apply the `RelativeOffset` when rendering at different target resolutions.

## Acceptance Criteria
- New captions start at (0.5, 0.5) center.
- Resizing the window does not move the caption relative to its background/container ratio.
- Exported PNGs match the relative layout seen in the Playground.
