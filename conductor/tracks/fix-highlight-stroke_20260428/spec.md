# Specification: Fix Highlight Double Stroke Bug

## Overview
The "double stroke" effect (inner and outer strokes) is not being correctly applied to highlighted text segments in `FancyCaptionView`. Only the font color of the highlight is currently drawn, and the strokes are completely missing. This issue has been observed primarily on the Desktop (Playground) platform. The root cause is likely within the Canvas rendering logic, as `AnnotatedString` does not natively support custom multiple strokes parameters.

## Functional Requirements
- Modify the rendering logic in `FancyCaptionView` (and its underlying rendering functions, like `CaptionRender.kt`) to ensure that both inner and outer strokes are correctly drawn for highlighted segments.
- Implement a custom drawing routine or workaround to apply the highlight's specific double stroke parameters during the Canvas draw phase, since `AnnotatedString`'s `SpanStyle` may not fully support this.
- Maintain the normal double stroke effect for non-highlighted text simultaneously.

## Acceptance Criteria
- When a caption with a highlighted segment is rendered, the highlighted portion must visibly display its specified inner stroke and outer stroke colors and widths.
- The base double stroke style (if enabled) must also remain functional and visible for non-highlighted segments.
- The fix should resolve the issue cross-platform (Playground, Android, iOS).

## Out of Scope
- Adding new caption styling features beyond fixing the existing highlight double stroke effect.