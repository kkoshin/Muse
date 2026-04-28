# Implementation Plan: Fix Highlight Double Stroke Bug

## Phase 1: Test & Reproduce
- [ ] Task: Write Failing Test for Highlight Stroke
    - [ ] Add unit tests verifying how highlight segments with double stroke styles are configured and translated to the rendering input.
- [ ] Task: Conductor - User Manual Verification 'Test & Reproduce' (Protocol in workflow.md)

## Phase 2: Fix Rendering Logic
- [ ] Task: Update `CaptionRender.kt`
    - [ ] Modify `drawCaption` to iterate over segments and handle individual text layouts if `AnnotatedString` does not natively support stroke properties.
    - [ ] Draw the outer stroke (`textStrokeColorExt`, `textStrokeWidthExt`) and inner stroke (`textStrokeColor`, `textStrokeWidth`) properly for both the base style and the highlighted segments.
- [ ] Task: Conductor - User Manual Verification 'Fix Rendering Logic' (Protocol in workflow.md)