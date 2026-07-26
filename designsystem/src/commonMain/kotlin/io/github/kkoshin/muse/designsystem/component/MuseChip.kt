@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.kkoshin.muse.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.SelectableChipElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape

/**
 * Muse Design System — Filter chip.
 *
 * Thin wrapper over M3 [FilterChip]. Maps to the existing M2 [Chip] usage
 * (single-selection chip with leading/trailing icon).
 */
@Composable
fun MuseFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable () -> Unit = {},
    trailingIcon: @Composable () -> Unit = {},
    shape: Shape = FilterChipDefaults.shape,
    colors: SelectableChipColors = FilterChipDefaults.filterChipColors(),
    elevation: SelectableChipElevation? = FilterChipDefaults.filterChipElevation(),
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource? = null,
    label: @Composable () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        interactionSource = interactionSource ?: remember { MutableInteractionSource() },
        label = label,
    )
}
