# Specification: Fix Stroke Gap on FontScale Adjustment

## Overview
When `fontScale` is adjusted, a visible gap appears between the text fill and its double strokes. This is caused by `TextMeasurer` producing slightly different layouts (e.g., kerning differences) for the fill and stroke layers because they are generated using `AnnotatedString`s with different internal `SpanStyle` structures.

## Functional Requirements
- Unify the `AnnotatedString` generation for both fill and stroke layers so they produce the exact same `SpanStyle` boundaries.
- Ensure that the base `CaptionStyle` is properly passed to the fill's `AnnotatedString` to inherit the correct base text color.
- Ensure stroke widths scale proportionally with `fontScale` if necessary, or at least ensure the paths align perfectly by using identical text layouts.

## Acceptance Criteria
- Adjusting `fontScale` does not result in any gaps or misalignments between the inner stroke, outer stroke, and the text fill.
- The rendering perfectly overlaps cross-platform.