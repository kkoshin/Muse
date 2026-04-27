# Implementation Plan: Rich Text Captions (Highlight Support)

## Phase 1: Data Model and Highlighting Logic [checkpoint: f855502]
- [x] Task: Update `CaptionStyle` to include a default `highlightStyle` property.
- [x] Task: Create a `CaptionSegment` data model to represent text spans and their associated styling overrides.
- [x] Task: TDD - Write unit tests for a new text processing utility that splits text by lines and identifies the middle character(s) to create a list of `CaptionSegment`s.
- [x] Task: Implement the text processing utility to make the tests pass.
- [x] Task: Conductor - User Manual Verification 'Phase 1: Data Model and Highlighting Logic' (Protocol in workflow.md)

## Phase 2: Rendering Integration [checkpoint: 5a04cde]
- [x] Task: Modify rendering logic (e.g., in `CaptionRender.kt` and extensions) to convert `CaptionSegment`s into a Compose `AnnotatedString` with appropriate `SpanStyle`s during measurement and drawing.
- [x] Task: Update `CaptionView.kt` to process the default caption text using the new utility and render the `AnnotatedString`.
- [x] Task: Conductor - User Manual Verification 'Phase 2: Rendering Integration' (Protocol in workflow.md)