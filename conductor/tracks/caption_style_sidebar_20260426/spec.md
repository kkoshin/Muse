# Specification: CaptionView Style Adjustment Sidebar

## Overview
Add a style adjustment sidebar to the `CaptionView` in the `io.github.kkoshin.muse.playground.ui` package. The sidebar will be docked to the right side of the screen and allow users to modify `CaptionStyle` properties, with the changes reflecting in real-time on the main preview canvas.

## Functional Requirements
- **Positioning:** A sidebar panel docked to the right side of the `CaptionView`.
- **Layout:** All style adjustment properties will be presented in a single, vertically scrolling list.
- **Supported Properties:** The sidebar must support adjustments for all properties in `CaptionStyle`:
  - `textColor` (Color)
  - `border` (Color, Width)
  - `background` (Color, Content Padding, Radius)
- **UI Controls:** 
  - Provide Color Pickers (or color selection mechanisms) for all color properties.
  - Provide Sliders for continuous adjustments (Width, Content Padding, Radius).
  - Provide Input Fields alongside sliders for precise numeric input.
- **Real-time Preview:** Any changes made in the sidebar, especially while dragging sliders, must immediately update the `CaptionView` canvas to show the new text effect without lag.

## Non-Functional Requirements
- **Performance:** Rendering the preview during rapid state changes (e.g., slider dragging) must be smooth.
- **UI Consistency:** The sidebar should utilize standard Compose Material components for a native look.

## Acceptance Criteria
- User can see the sidebar on the right when `CaptionView` is open.
- User can change the text color, border properties, and background properties.
- User can use a slider, input field, or color picker to adjust values.
- Dragging a slider updates the text preview in real-time on the main canvas.

## Out of Scope
- Saving/loading customized styles to/from a database or presets file.
- Adding new properties to the underlying `CaptionStyle` data class.