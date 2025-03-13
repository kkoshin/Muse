package io.github.kkoshin.muse.feature.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.kkoshin.muse.core.provider.Voice
import kotlinx.serialization.Serializable

@Serializable
data class EditorArgs(
    val scriptId: String,
)

/**
 * 1. show processing progress
 * 2. config silence duration
 * 3. request to export as mp3
 */
@Composable
expect fun EditorScreen(
    modifier: Modifier = Modifier,
    args: EditorArgs,
    viewModel: EditorViewModel,
    onExportRequest: (List<Voice>) -> Unit,
    onPickVoice: () -> Unit,
)