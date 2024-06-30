package io.github.kkoshin.muse.setting.voice

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Checkbox
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.foodiestudio.sugar.notification.toast
import io.github.kkoshin.muse.tts.TTSManager
import io.github.kkoshin.muse.tts.Voice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import muse.composeapp.generated.resources.*
import muse.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.rememberKoinInject

@Serializable
class VoicePickerArgs(
    val selectedVoiceIds: List<String>,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VoicePicker(
    modifier: Modifier = Modifier,
    selectedVoiceIds: Set<String>,
    onBack: () -> Unit,
) {
    var voices: List<Voice> by remember {
        mutableStateOf(emptyList())
    }

    val selected = remember {
        selectedVoiceIds.toMutableStateList()
    }

    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val ttsManager = rememberKoinInject<TTSManager>()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    BackHandler {
        scope.launch {
            ttsManager.updateAvailableVoice(selected.toSet())
            onBack()
        }
    }

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
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.statusBars,
                backgroundColor = MaterialTheme.colors.surface,
                navigationIcon = {
                    IconButton(onClick = {
                        backPressedDispatcher?.onBackPressed()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                title = {
                    Text(text = stringResource(Res.string.voices))
                },
                actions = {
                    IconButton(
                        enabled = voices.isNotEmpty(),
                        onClick = {
                            selected.clear()
                        },
                    ) {
                        Icon(Icons.Default.Deselect, "deselect All")
                    }
                    IconButton(
                        enabled = voices.isNotEmpty(),
                        onClick = {
                            selected.clear()
                            selected.addAll(voices.map { it.voiceId })
                        },
                    ) {
                        Icon(Icons.Default.SelectAll, "select All")
                    }
                },
            )
        },
        content = { contentPadding ->
            Column(modifier = modifier.padding(contentPadding)) {
                LazyColumn {
                    voices
                        .groupBy { it.accent }
                        .toSortedMap()
                        .forEach { (accent, voicesList) ->
                            stickyHeader {
                                Text(
                                    accent.name,
                                    style = MaterialTheme.typography.subtitle1,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFEDEDED))
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                )
                            }
                            items(voicesList) {
                                VoiceItem(it, selected.toSet()) { voice, isChecked ->
                                    if (isChecked) {
                                        selected.add(voice.voiceId)
                                    } else {
                                        selected.remove(voice.voiceId)
                                    }
                                }
                            }
                        }
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun VoiceItem(
    voice: Voice,
    selectedVoiceIds: Set<String>,
    onSelected: (Voice, selected: Boolean) -> Unit,
) {
    ListItem(
        text = { Text(text = getAccentFlag(voice.accent) + " " + voice.name) },
        secondaryText = {
            listOfNotNull(voice.gender?.raw, voice.age?.raw, voice.description)
                .joinToString("・")
                .let {
                    Text(it)
                }
        },
        trailing = {
            Checkbox(selectedVoiceIds.contains(voice.voiceId), onCheckedChange = { isChecked ->
                onSelected(voice, isChecked)
            })
        },
    )
}

private fun getAccentFlag(accent: Voice.Accent): String =
    when (accent) {
        Voice.Accent.American -> "🇺🇸"
        Voice.Accent.British -> "🇬🇧"
        Voice.Accent.BritishSwedish -> "🇸🇪"
        Voice.Accent.Australian -> "🇦🇺"
        Voice.Accent.Irish -> "🇮🇪"
        Voice.Accent.Other -> "❓"
    }
