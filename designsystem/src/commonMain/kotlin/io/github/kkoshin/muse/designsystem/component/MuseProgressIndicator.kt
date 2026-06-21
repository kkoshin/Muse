package io.github.kkoshin.muse.designsystem.component

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/**
 * Muse Design System — Circular progress indicator.
 *
 * Thin wrapper over M3 [CircularProgressIndicator].
 * Pass [progress] = [Float.NaN] (default) for indeterminate mode.
 */
@Composable
fun MuseCircularProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float = Float.NaN,
    color: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth,
    trackColor: Color = ProgressIndicatorDefaults.circularTrackColor,
) {
    CircularProgressIndicator(
        modifier = modifier,
        progress = progress,
        color = color,
        strokeWidth = strokeWidth,
        trackColor = trackColor,
    )
}
