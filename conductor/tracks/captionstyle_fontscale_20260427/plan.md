# Implementation Plan: CaptionStyle fontScale Support

## Phase 1: Data Model Update [checkpoint: 28ed744]
- [x] Task: Update `CaptionStyle` data class
    - [ ] Add `val fontScale: Float = 1.0f` to the `CaptionStyle` definition.
- [x] Task: Conductor - User Manual Verification 'Phase 1: Data Model Update' (Protocol in workflow.md)

## Phase 2: Update Rendering Logic [checkpoint: cf45aff]
- [x] Task: Apply `fontScale` in `drawCaption`
    - [x] Modify `drawCaption` in `CaptionRender.kt` to dynamically calculate the `fontSize` for the text.
    - [x] Calculate the final font size using the formula: `baseFontSize * caption.style.fontScale * density`.
    - [x] Ensure that only the text size is scaled; background and border DP properties must remain absolute.
- [x] Task: Conductor - User Manual Verification 'Phase 2: Update Rendering Logic' (Protocol in workflow.md)

## Phase 3: UI Integration [checkpoint: ccbab1d]
- [x] Task: Add slider to Style Sidebar
    - [x] Modify `CaptionView.kt` to include a `NumericSlider` labeled "Font Scale" in the Style Sidebar.
    - [x] Bind the slider to update `captionStyle.fontScale`.
    - [x] Configure the slider to have a reasonable range (e.g., 0.5f to 3.0f).
- [x] Task: Conductor - User Manual Verification 'Phase 3: UI Integration' (Protocol in workflow.md)
