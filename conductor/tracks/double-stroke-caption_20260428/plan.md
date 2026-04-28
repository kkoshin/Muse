# Implementation Plan: Double Stroke Caption Style

## Phase 1: Core Logic & Tests
- [x] Task: Update Caption Style Model
    - [x] Update style data class/model to include `textStrokeColorExt` and `textStrokeWidthExt`.
- [x] Task: Write Tests for Double Stroke Model
    - [x] Add unit tests verifying default values and property assignment for new double stroke properties.
- [x] Task: Implement Double Stroke Rendering
    - [x] Update `FancyCaptionView` rendering logic to draw the outer stroke (`textStrokeColorExt`, `textStrokeWidthExt`) behind the inner stroke, using the provided reference code.
- [x] Task: Implement Highlight Default Behavior
    - [x] Update logic so when `Highlight` is enabled, it defaults to Inner Stroke: Purple 2dp, Outer Stroke: White 4dp.
- [x] Task: Conductor - User Manual Verification 'Core Logic & Tests' (Protocol in workflow.md)

## Phase 2: Playground UI Updates
- [ ] Task: Update StyleSlider UI
    - [ ] Add editing inputs for inner stroke color and width in `StyleSlider`.
    - [ ] Add editing inputs for outer stroke color and width in `StyleSlider`.
- [ ] Task: Wire StyleSlider to Model
    - [ ] Connect the new UI controls to update the `FancyCaptionView` style model properties.
- [ ] Task: Conductor - User Manual Verification 'Playground UI Updates' (Protocol in workflow.md)