package io.github.kkoshin.muse.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Muse Design System — Scaffold.
 *
 * Thin wrapper over M3 [Scaffold] with design-system token defaults:
 * - `contentWindowInsets` = [WindowInsets.systemBars] (matches existing M2 pattern
 *   across all screens, removing the need to repeat it at each call site).
 *
 * All other parameters align with M3 [Scaffold] and default to M3 values,
 * which resolve through [MuseTheme]'s color scheme.
 */
@Composable
fun MuseScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    contentWindowInsets: WindowInsets = WindowInsets.systemBars,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        containerColor = containerColor,
        contentColor = contentColor,
        contentWindowInsets = contentWindowInsets,
        content = content,
    )
}
