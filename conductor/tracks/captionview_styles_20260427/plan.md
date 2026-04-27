# Implementation Plan: CaptionView Style Enhancements

## Phase 1: Data Model Updates
- [ ] Task: Create a `TextStyleOption` enum in the data layer containing `Normal`, `Bold`, `Italic`, and `Underline`.
- [ ] Task: Update the `CaptionStyle` data class to include `letterSpacing` (Float, default 0f) and `textStyle` (`TextStyleOption`, default `Normal`).
- [ ] Task: TDD - Write unit tests in `CaptionStyleTest.kt` to verify that the `toTextStyle` extension function correctly maps the new properties (mapping `letterSpacing` to `em` units, and the `TextStyleOption` to Compose's `FontWeight`, `FontStyle`, or `TextDecoration`).
- [ ] Task: Update the `toTextStyle` extension function in `Caption.kt` to implement the logic to make the tests pass.
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Data Model Updates' (Protocol in workflow.md)

## Phase 2: UI Updates (Sidebar)
- [ ] Task: Add a new `NumericSlider` for `letterSpacing` to the `CaptionView.kt` Sidebar, placed near the "Font Scale" control.
- [ ] Task: Add UI controls (e.g., a Row of toggleable buttons or text) to `CaptionView.kt` to allow the user to select the active `TextStyleOption`.
- [ ] Task: Update the `CaptionView.kt` state management to correctly apply these new options to the `captionStyle` state.
- [ ] Task: Conductor - User Manual Verification 'Phase 2: UI Updates' (Protocol in workflow.md)