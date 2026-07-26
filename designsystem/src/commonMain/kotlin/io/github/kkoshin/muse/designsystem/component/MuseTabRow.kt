@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.kkoshin.muse.designsystem.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Muse Design System — Tab row.
 *
 * Thin wrapper over M3 [TabRow] with design-system token defaults:
 * - `containerColor` = [MaterialTheme.colorScheme.surface] (matches existing
 *   M2 [TabRow] usage).
 */
@Composable
fun MuseTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    tabs: @Composable () -> Unit,
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
            )
        },
        divider = { HorizontalDivider() },
        tabs = tabs,
    )
}

/**
 * Muse Design System — Tab.
 *
 * Thin wrapper over M3 [Tab]. Colors resolve through [MuseTheme]'s color scheme.
 */
@Composable
fun MuseTab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selectedContentColor: Color = MaterialTheme.colorScheme.primary,
    unselectedContentColor: Color = MaterialTheme.colorScheme.onSurface,
    text: @Composable () -> Unit = {},
    icon: @Composable () -> Unit = {},
    interactionSource: MutableInteractionSource? = null,
) {
    Tab(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        text = text,
        icon = icon,
        interactionSource = interactionSource ?: remember { MutableInteractionSource() },
    )
}
