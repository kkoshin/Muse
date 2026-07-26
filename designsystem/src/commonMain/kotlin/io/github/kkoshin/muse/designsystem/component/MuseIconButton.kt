package io.github.kkoshin.muse.designsystem.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * Muse Design System — Icon button.
 *
 * Thin wrapper over M3 [IconButton]. The M3 default already enforces a 48 dp minimum
 * touch target via [Modifier.minimumInteractiveComponentSize].
 */
@Composable
fun MuseIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource ?: remember { MutableInteractionSource() },
        content = content,
    )
}
