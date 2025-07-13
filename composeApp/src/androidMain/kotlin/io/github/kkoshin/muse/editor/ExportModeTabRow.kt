package io.github.kkoshin.muse.editor

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ExportModeTabRow(
    modifier: Modifier = Modifier,
    selectedMode: ExportMode,
    onTabChanged: (ExportMode) -> Unit
) {
    val modes = ExportMode.entries.toList()
    TabRow(
        modifier = modifier,
        selectedTabIndex = modes.indexOf(selectedMode),
        backgroundColor = MaterialTheme.colors.surface
    ) {
        modes.forEach { mode ->
            Tab(
                selected = selectedMode == mode,
                onClick = { onTabChanged(mode) },
                text = {
                    Text(
                        text = mode.name
                    )
                }
            )
        }
    }
}