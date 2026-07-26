package io.github.kkoshin.muse.designsystem.foundation

import androidx.compose.animation.core.tween
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/**
 * Muse Design System — Shared component defaults.
 *
 * Centralizes commonly reused default values (animation durations, touch targets, etc.)
 * so individual components stay consistent without duplicating magic numbers.
 */
object ComponentDefaults {

    /** Standard animation duration for state transitions. */
    const val ANIMATION_DURATION_MS = 150

    /** Standard animation spec for state transitions. */
    fun <T> defaultTween() = tween<T>(durationMillis = ANIMATION_DURATION_MS)

    /** Minimum touch target size per accessibility guidelines. */
    val MIN_TOUCH_TARGET = 48.dp

    /** Standard content padding inside cards and containers. */
    val CONTENT_PADDING = 16.dp

    /** Standard icon size. */
    val ICON_SIZE = 24.dp

    /** Small icon size for compact layouts. */
    val ICON_SIZE_SMALL = 20.dp
}
