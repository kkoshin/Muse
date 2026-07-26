package io.github.kkoshin.muse.designsystem.foundation

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import io.github.kkoshin.muse.designsystem.theme.MuseSpacing

/**
 * Muse Design System — Modifier extensions.
 *
 * Convenience modifiers that apply design-system tokens, keeping business code concise.
 */

/** Horizontal padding using [MuseSpacing] tokens. */
fun Modifier.museHorizontalPadding(spacing: Dp = MuseSpacing.lg): Modifier =
    padding(horizontal = spacing)

/** Vertical padding using [MuseSpacing] tokens. */
fun Modifier.museVerticalPadding(spacing: Dp = MuseSpacing.lg): Modifier =
    padding(vertical = spacing)

/** Symmetric padding using [MuseSpacing] tokens. */
fun Modifier.musePadding(
    horizontal: Dp = MuseSpacing.lg,
    vertical: Dp = MuseSpacing.lg,
): Modifier = padding(horizontal = horizontal, vertical = vertical)
