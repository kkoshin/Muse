# Specification: Double Stroke Caption Style

## Overview
Enhance the `FancyCaptionView` component to support a "double stroke" text style. This allows text to have both an inner and an outer stroke for better visibility and aesthetic appeal. The `Playground` module will also be updated to expose these new styling options in the `StyleSlider`.

## Functional Requirements

### 1. FancyCaptionView Enhancements
- Introduce support for an external (outer) stroke in addition to the existing internal (inner) stroke.
- Expose properties to configure:
  - `textStrokeColor`: Color of the inner stroke.
  - `textStrokeWidth`: Width of the inner stroke.
  - `textStrokeColorExt`: Color of the outer stroke.
  - `textStrokeWidthExt`: Width of the outer stroke.
- **Reference Implementation:**
  ```kotlin
  if (style.textStrokeColor != Color.TRANSPARENT) {
      textPaint.style = Paint.Style.STROKE
      textPaint.color = style.textStrokeColor
      textPaint.strokeWidth = style.textStrokeWidth
      staticLayout.draw(canvas)
  }
  if (style.textStrokeColorExt != Color.TRANSPARENT) {
      textPaint.style = Paint.Style.STROKE
      textPaint.color = style.textStrokeColorExt
      textPaint.strokeWidth = style.textStrokeWidthExt
      staticLayout.draw(canvas)
  }
  ```

### 2. Playground Module Updates
- Update the `StyleSlider` in the Playground module to include editing inputs for the new double stroke style.
- Provide separate controls for both inner and outer stroke colors and widths.

### 3. Highlight Behavior
- The Highlight property must default to enabling the double stroke effect.
- The highlight state will use a **fixed color combination** for its double stroke.
- Default configuration when enabled:
  - Inner Stroke: Purple, 2dp
  - Outer Stroke: White, 4dp

## Non-Functional Requirements
- Ensure drawing performance is not noticeably degraded by the additional canvas draw calls.
- Maintain backward compatibility with existing styles that do not use the double stroke.

## Out of Scope
- Configurable colors/widths for the highlight double stroke (it uses fixed colors).
- Other new text effects (e.g., shadows, gradients) beyond the double stroke.