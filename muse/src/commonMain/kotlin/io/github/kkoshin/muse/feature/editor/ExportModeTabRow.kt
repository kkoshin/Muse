package io.github.kkoshin.muse.editor

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.kkoshin.muse.designsystem.component.MuseTab
import io.github.kkoshin.muse.designsystem.component.MuseTabRow
import io.github.kkoshin.muse.feature.editor.ExportMode

@Composable
fun ExportModeTabRow(
    modifier: Modifier = Modifier,
    selectedMode: ExportMode,
    onTabChanged: (ExportMode) -> Unit
) {
    val modes = ExportMode.entries.toList()
    MuseTabRow(
        modifier = modifier,
        selectedTabIndex = modes.indexOf(selectedMode),
    ) {
        modes.forEach { mode ->
            MuseTab(
                selected = selectedMode == mode,
                onClick = { onTabChanged(mode) },
                text = {
                    Text(text = mode.name)
                }
            )
        }
    }
}
