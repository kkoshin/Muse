package io.github.kkoshin.muse.designsystem.theme

import androidx.compose.ui.unit.dp

/**
 * Muse Design System — Elevation tokens.
 *
 * Standardized elevation values to replace scattered hardcoded `.dp` values.
 */
object MuseElevation {
    /** 0.dp — flat surfaces (e.g. TopAppBar, Cards on surface) */
    val none = 0.dp
    /** 2.dp — subtle lift (e.g. Cards at rest) */
    val low = 2.dp
    /** 4.dp — moderate lift (e.g. floating elements) */
    val medium = 4.dp
    /** 8.dp — high lift (e.g. BottomSheets, Dialogs) */
    val high = 8.dp
    /** 12.dp — maximum lift (e.g. active FAB) */
    val highest = 12.dp
}
