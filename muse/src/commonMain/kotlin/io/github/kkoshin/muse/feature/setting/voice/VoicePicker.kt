package io.github.kkoshin.muse.feature.setting.voice

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable

@Serializable
class VoicePickerArgs(
    val selectedVoiceIds: List<String>,
)

@Composable
expect fun VoicePicker(
    modifier: Modifier = Modifier,
    selectedVoiceIds: Set<String>,
    onBack: () -> Unit,
)