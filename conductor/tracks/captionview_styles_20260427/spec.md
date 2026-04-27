# Specification: CaptionView Style Enhancements

## Overview
Add new typography controls to the `CaptionView` in the `Playground` module. The new features include adjustable letter spacing and the ability to apply mutually exclusive text styles (Bold, Italic, Underline). The `StyleSidebar` will be updated to expose these adjustments.

## Core Requirements

### 1. Data Model Updates (`CaptionStyle`)
- **Letter Spacing:** Add a `letterSpacing` property measured in `em` units (relative to font size) to ensure it scales correctly when the `fontScale` changes.
- **Text Styles:** Add support for mutually exclusive text styles (Bold, Italic, Underline). A single enum (e.g., `enum class TextStyleOption { Normal, Bold, Italic, Underline }`) could be added to `CaptionStyle` to enforce this mutual exclusivity at the data level.

### 2. Rendering Updates (`CaptionRender.kt` / `Caption.kt`)
- Update the `toTextStyle` extension function to apply the new `letterSpacing` (using `em`), and map the selected `TextStyleOption` to the appropriate Compose `FontWeight`, `FontStyle`, or `TextDecoration` within the `TextStyle`.

### 3. UI Updates (`CaptionView.kt` / Sidebar)
- **Letter Spacing Slider:** Add a new `NumericSlider` in the sidebar to adjust the `letterSpacing` value (e.g., range `0.0f` to `1.0f`).
- **Style Selection:** Add UI controls (e.g., a row of toggle buttons or a dropdown) to select the active text style.
- **UI Grouping:** Group both the letter spacing slider and the text style selectors near the existing "Font Scale" slider in the sidebar.

## Out of Scope
- Applying these styles (bold, italic, underline, letter spacing) selectively to individual words within the caption; these enhancements apply globally to the entire caption.