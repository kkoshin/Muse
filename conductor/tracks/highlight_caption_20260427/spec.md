# Specification: Rich Text Captions (Highlight Support)

## Overview
Enhance the `Caption` system in the `Playground` module to support rich text formatting. The initial MVP will implement a "Highlight Caption" feature to validate the extensible rich text architecture. This feature will apply a distinct highlight style (larger font, red color) to the middle character(s) of every line in the caption.

## Core Requirements

### 1. Extensible Rich Text Architecture
- **Data Model:** Introduce a custom segment model (e.g., a list of text segments with associated styles) to represent rich text in a platform-agnostic way. This model will be converted to Compose's `AnnotatedString` during rendering.
- **Interface Design:** The design must allow for easy addition of new text effects and styles in the future without major architectural rewrites.

### 2. Highlight Feature (MVP)
- **Style Definition:** Add an extensible `highlightStyle` property to the `CaptionStyle` (or a similar construct) with default values: `fontScale = 2.0f` and `textColor = Color.Red`.
- **Highlighting Logic:** Automatically apply the `highlightStyle` to the middle character(s) of *each line* in the caption text.
  - If the line length is odd, highlight the exact middle character.
  - If the line length is even, highlight the two middle characters.
- **Default Style:** All characters not highlighted will use the standard text style defined in the caption.

## Out of Scope
- UI controls in the Sidebar to modify the `highlightStyle` properties or toggle the feature on/off (for the MVP, the logic will be applied programmatically based on line splitting).
- Complex parsing of markdown or HTML tags in the text (the logic applies programmatically).