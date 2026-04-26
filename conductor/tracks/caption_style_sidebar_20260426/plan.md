# Implementation Plan: CaptionView Style Adjustment Sidebar

## Phase 1: Layout & State Preparation
- [ ] Task: Refactor `CaptionView` to manage mutable state for `CaptionStyle`.
- [ ] Task: Update the `CaptionView` layout to include a right-side panel for the sidebar alongside the main canvas.
- [ ] Task: Conductor - User Manual Verification 'Layout & State Preparation' (Protocol in workflow.md)

## Phase 2: Reusable UI Controls
- [ ] Task: Implement a reusable Color Picker component for the sidebar.
- [ ] Task: Implement a reusable Slider with Numeric Input component for continuous/precise adjustments.
- [ ] Task: Conductor - User Manual Verification 'Reusable UI Controls' (Protocol in workflow.md)

## Phase 3: Sidebar Assembly & Integration
- [ ] Task: Create the main Sidebar composable with a vertically scrolling list.
- [ ] Task: Integrate controls for `textColor`.
- [ ] Task: Integrate controls for `border` (color, width).
- [ ] Task: Integrate controls for `background` (color, contentPadding, radius).
- [ ] Task: Connect sidebar actions to update the main `CaptionStyle` state, ensuring real-time preview on the canvas.
- [ ] Task: Conductor - User Manual Verification 'Sidebar Assembly & Integration' (Protocol in workflow.md)