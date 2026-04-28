# Implementation Plan: Fix Stroke Gap on FontScale Adjustment

## Phase 1: Unify AnnotatedString Structure
- [ ] Task: Update `Caption.kt`
    - [ ] Modify `toAnnotatedString` to ensure both fill and stroke variants push `SpanStyle`s consistently for every segment.
    - [ ] Update the default fill `toAnnotatedString` to accept and use the base `CaptionStyle` for correct text color inheritance.
- [ ] Task: Update `CaptionRender.kt`
    - [ ] Update `drawCaption` to use the new `toAnnotatedString` signature for the fill layout.
    - [ ] Scale the stroke widths by the effective `fontScale` to ensure they remain proportional to the text size.
- [ ] Task: Conductor - User Manual Verification 'Unify AnnotatedString Structure' (Protocol in workflow.md)