# Implementation Plan: Fix Highlight Double Stroke Bug

## Phase 1: Test & Reproduce [checkpoint: b655451]
- [x] Task: Write Failing Test for Highlight Stroke
    - [x] Add unit tests verifying how highlight segments with double stroke styles are configured and translated to the rendering input.
- [x] Task: Conductor - User Manual Verification 'Test & Reproduce' (Protocol in workflow.md)

## Phase 2: Fix Rendering Logic [checkpoint: 34690ea]
- [x] Task: Update `CaptionRender.kt`
    - [x] Modify `drawCaption` to iterate over segments and handle individual text layouts if `AnnotatedString` does not natively support stroke properties.
    - [x] Draw the outer stroke (`textStrokeColorExt`, `textStrokeWidthExt`) and inner stroke (`textStrokeColor`, `textStrokeWidth`) properly for both the base style and the highlighted segments.
- [x] Task: Conductor - User Manual Verification 'Fix Rendering Logic' (Protocol in workflow.md)
