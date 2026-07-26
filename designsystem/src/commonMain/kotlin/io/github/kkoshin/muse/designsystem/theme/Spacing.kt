package io.github.kkoshin.muse.designsystem.theme

import androidx.compose.ui.unit.dp

/**
 * Muse Design System — Spacing tokens.
 *
 * All spacing values in the app should derive from these constants
 * rather than using inline `.dp` literals.
 */
object MuseSpacing {
    /** 4.dp — tight inner padding, icon gaps */
    val xs = 4.dp
    /** 8.dp — default inner padding, small gaps */
    val sm = 8.dp
    /** 12.dp — medium gaps, card inner padding */
    val md = 12.dp
    /** 16.dp — standard outer padding, list item padding */
    val lg = 16.dp
    /** 24.dp — section spacing, large outer padding */
    val xl = 24.dp
    /** 32.dp — major section dividers */
    val xxl = 32.dp
    /** 48.dp — hero spacing, large component gaps */
    val xxxl = 48.dp
}
