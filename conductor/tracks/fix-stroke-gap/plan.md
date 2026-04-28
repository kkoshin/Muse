# Implementation Plan: Fix Stroke Gap on FontScale Adjustment

## Phase 1: Unify AnnotatedString Structure [checkpoint: 037abaf]
- [x] Task: Update `Caption.kt`
    - [x] Modify `toAnnotatedString` to ensure both fill and stroke variants push `SpanStyle`s consistently for every segment.
    - [x] Update the default fill `toAnnotatedString` to accept and use the base `CaptionStyle` for correct text color inheritance.
- [x] Task: Update `CaptionRender.kt`
    - [x] Update `drawCaption` to use the new `toAnnotatedString` signature for the fill layout.
    - [x] Scale the stroke widths by the effective `fontScale` to ensure they remain proportional to the text size.
- [x] Task: Conductor - User Manual Verification 'Unify AnnotatedString Structure' (Protocol in workflow.md)