# Specification: CaptionStyle fontScale Support

## Overview
Add support for `fontScale` in `CaptionStyle` to allow individual scaling of caption text size. The final rendered font size will be calculated based on a base font size, the `fontScale` value, and the display density. The Style Sidebar will also be updated to include a slider control for adjusting this scale.

## Functional Requirements
1. **Data Model Update:**
   - Add a `fontScale` property (Float, default 1.0f) to `CaptionStyle`.

2. **Rendering Logic:**
   - The final rendered `fontSize` must be dynamically calculated as: `baseFontSize * fontScale * density`.
   - Only the font size should scale; background padding, background radius, border width, and border radius must remain at their absolute DP values.

3. **UI Integration (Style Sidebar):**
   - Add a `NumericSlider` component in the Style Sidebar to control the `fontScale` property of the current `CaptionStyle`.
   - The slider should have a reasonable range (e.g., 0.5f to 3.0f).

## Non-Functional Requirements
- The change must integrate seamlessly with the existing `CaptionTransform` and preview scaling logic introduced previously (which scales the entire caption layout for preview purposes).

## Acceptance Criteria
- [ ] A `fontScale` slider is visible and functional in the Style Sidebar.
- [ ] Adjusting the `fontScale` slider immediately updates the text size of the caption in the preview.
- [ ] Changing `fontScale` does not inadvertently alter the absolute DP values of the caption's background padding or border width.
- [ ] Exported images correctly reflect the applied `fontScale` at the target resolution.

## Out of Scope
- Global font scaling (this is applied per `CaptionStyle`).
- Proportional scaling of padding/border based on font scale (these remain absolute).
