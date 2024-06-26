package io.github.kkoshin.muse.tts.voice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.github.foodiestudio.sugar.notification.toast
import io.github.kkoshin.muse.tts.TTSManager
import io.github.kkoshin.muse.tts.Voice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.koin.compose.rememberKoinInject

@Serializable
class VoicePickerArgs(
    val selectedVoiceId: String? = null,
)

@Composable
fun VoicePicker(
    modifier: Modifier = Modifier,
    selectedVoiceId: String?,
    onSelected: (String) -> Unit,
) {
    var voices: List<Voice> by remember {
        mutableStateOf(emptyList())
    }

    var selectedVoice: Voice? by remember {
        mutableStateOf(voices.find { it.voiceId == selectedVoiceId })
    }

    val ttsManager = rememberKoinInject<TTSManager>()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        ttsManager
            .queryVoiceList()
            .onSuccess {
                voices = it
            }.onFailure {
                withContext(Dispatchers.Main) {
                    context.toast(it.message ?: "unknown error")
                }
            }
    }
    Column(modifier = modifier) {
        AudioPlayer(url = selectedVoice?.previewUrl)
        LazyColumn {
            items(voices) {
                VoiceItem(it, selectedVoiceId) { voice ->
                    selectedVoice = voice
                }
            }
        }
    }
}

@Composable
private fun AudioPlayer(
    modifier: Modifier = Modifier,
    url: String?,
) {
    Text("AudioPlayer: $url")
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun VoiceItem(
    voice: Voice,
    selectedVoiceId: String?,
    onSelected: (Voice) -> Unit,
) {
    val selected = selectedVoiceId == voice.voiceId
    ListItem(
        text = { Text(text = voice.name) },
        secondaryText = { Text(text = voice.description ?: "") },
        icon = {
            IconButton(enabled = !selected, onClick = {
                onSelected(voice)
            }) {
                Icon(Icons.Filled.PlayArrow, null)
            }
        },
        trailing = {
            if (selectedVoiceId == voice.voiceId) {
                Icon(Icons.Filled.Check, null)
            }
        },
    )
}