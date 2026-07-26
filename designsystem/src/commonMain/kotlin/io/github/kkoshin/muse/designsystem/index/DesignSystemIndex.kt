/**
 * Muse Design System — Component Index.
 *
 * ## Package structure
 * ```
 * io.github.kkoshin.muse.designsystem
 *   theme/       — MuseTheme, MuseColor, MuseShapes, MuseTypography, MuseSpacing, MuseElevation
 *   foundation/  — ComponentDefaults, ModifierExt
 *   component/   — MuseTopAppBar, MuseScaffold, MuseButton, …  (this page)
 *   index/       — DesignSystemIndex (this file)
 * ```
 *
 * ## Quick reference
 *
 * | Import                                          | Component                   |
 * |-------------------------------------------------|-----------------------------|
 * | `component.MuseTopAppBar`                      | Top app bar                 |
 * | `component.MuseScaffold`                       | Page layout scaffold        |
 * | `component.MuseButton`                         | Filled button               |
 * | `component.MuseOutlinedButton`                 | Outlined button             |
 * | `component.MuseTextButton`                     | Text button                 |
 * | `component.MuseIconButton`                     | Icon button (48 dp target)  |
 * | `component.MuseSwitch`                         | Switch toggle               |
 * | `component.MuseOutlinedTextField`              | Text input field            |
 * | `component.MuseCard`                           | Elevated card               |
 * | `component.MuseAlertDialog`                    | Alert dialog                |
 * | `component.MuseFilterChip`                     | Filter chip                 |
 * | `component.MuseSlider`                         | Slider                      |
 * | `component.MuseTabRow`                         | Tab row                     |
 * | `component.MuseCircularProgressIndicator`      | Circular progress spinner   |
 * | `component.MuseTab`                            | Individual tab              |
 * | `foundation.ComponentDefaults`                 | Animation durations, sizes  |
 * | `foundation.musePadding` / `museHorizontalPadding` / `museVerticalPadding` | Spacing  |
 * | `theme.MuseTheme`                              | Root theme composable       |
 * | `theme.MuseColor`                              | Color tokens                |
 * | `theme.MuseShapes` / `MuseCorner`             | Shape tokens                |
 * | `theme.MuseTypography`                         | Type-scale tokens           |
 * | `theme.MuseSpacing`                            | Spacing tokens              |
 * | `theme.MuseElevation`                          | Elevation tokens            |
 *
 * ## Usage
 * ```kotlin
 * import io.github.kkoshin.muse.designsystem.theme.MuseTheme
 * import io.github.kkoshin.muse.designsystem.component.MuseTopAppBar
 * import io.github.kkoshin.muse.designsystem.component.MuseScaffold
 *
 * MuseTheme {
 *     MuseScaffold(
 *         topBar = { MuseTopAppBar(title = { Text("Hello") }) }
 *     ) { padding ->
 *         // content
 *     }
 * }
 * ```
 */
package io.github.kkoshin.muse.designsystem.index
