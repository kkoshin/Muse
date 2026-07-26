package io.github.kkoshin.muse.designsystem.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * Muse Design System — Switch.
 *
 * Thin wrapper over M3 [Switch]. Colors resolve through [SwitchDefaults.colors],
 * which reads from [MuseTheme]'s color scheme (primary for checked thumb,
 * matching the current M2 pattern of [SwitchDefaults.colors(checkedThumbColor = ...)]).
 */
@Composable
fun MuseSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource ?: remember { MutableInteractionSource() },
    )
}
