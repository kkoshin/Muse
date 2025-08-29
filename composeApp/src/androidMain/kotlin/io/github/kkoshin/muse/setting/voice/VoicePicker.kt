package io.github.kkoshin.muse.setting.voice

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.github.foodiestudio.sugar.notification.toast
import io.github.kkoshin.muse.audio.ui.AudioPlaybackButton
import io.github.kkoshin.muse.tts.TTSManager
import io.github.kkoshin.muse.tts.Voice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import muse.composeapp.generated.resources.Res
import muse.composeapp.generated.resources.voices
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.rememberKoinInject

@Serializable
class VoicePickerArgs(
    val selectedVoiceIds: List<String>,
)

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
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

    var previewVoice: Voice? by remember {
        mutableStateOf(null)
    }
    var playbackBarVisible by remember { mutableStateOf(false) }

    BackHandler {
        scope.launch {
            ttsManager.updateAvailableVoice(selected.toSet())
            onBack()
        }
    }

    DisposableEffect(playbackBarVisible) {
        // make three button style navigation transparent
        (context as Activity).window.isNavigationBarContrastEnforced = !playbackBarVisible
        onDispose {
            if (context.window.isNavigationBarContrastEnforced) {
                context.window.isNavigationBarContrastEnforced = false
            }
        }
    }

    LaunchedEffect(Unit) {
        ttsManager
            .queryVoiceList(true)
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
            LazyColumn(contentPadding = contentPadding) {
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
                                    .background(
                                        if (MaterialTheme.colors.isLight) Color(
                                            0xFFEDEDED
                                        ) else Color.DarkGray
                                    )
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                            )
                        }
                        items(voicesList) {
                            VoiceItem(it, selected.toSet(), onSelected = { voice, isChecked ->
                                if (isChecked) {
                                    selected.add(voice.voiceId)
                                } else {
                                    selected.remove(voice.voiceId)
                                }
                            }, onClick = { voice ->
                                previewVoice = voice
                                playbackBarVisible = true
                            })
                        }
                    }
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = playbackBarVisible,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            ) {
                previewVoice?.let { voice ->
                    PlaybackBar(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colors.secondary,
                                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                            )
                            .navigationBarsPadding(),
                        voice = voice,
                        onClose = {
                            playbackBarVisible = false
                        },
                    )
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
    onClick: (Voice) -> Unit,
) {
    ListItem(
        modifier = Modifier.clickable {
            onClick(voice)
        },
        text = {
            Text(
                text = getAccentFlag(voice.accent) + " " + voice.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        secondaryText = {
            Column {
                listOfNotNull(voice.gender?.raw, voice.age?.raw, voice.descriptive)
                    .joinToString("・")
                    .let {
                        Text(it)
                    }
            }
        },
        trailing = {
            Checkbox(selectedVoiceIds.contains(voice.voiceId), onCheckedChange = { isChecked ->
                onSelected(voice, isChecked)
            })
        },
    )
}

internal fun getAccentFlag(accent: Voice.Accent): String =
    when (accent) {
        Voice.Accent.American -> "🇺🇸"
        Voice.Accent.British -> "🇬🇧"
        Voice.Accent.BritishSwedish -> "🇸🇪"
        Voice.Accent.Australian -> "🇦🇺"
        Voice.Accent.Irish -> "🇮🇪"
        Voice.Accent.Transatlantic -> "🇺🇸"
        Voice.Accent.Other -> "❓"
    }

@Composable
fun PlaybackBar(modifier: Modifier = Modifier, voice: Voice, onClose: () -> Unit) {
    Row(
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {}
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))
        Text(getAccentFlag(voice.accent), style = MaterialTheme.typography.h5)
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(voice.name, style = MaterialTheme.typography.body1)
            listOfNotNull(voice.gender?.raw, voice.age?.raw, voice.descriptive)
                .joinToString("・")
                .let { Text(it, style = MaterialTheme.typography.caption) }
        }
        AudioPlaybackButton(audioSource = voice.previewUrl.toUri())
        IconButton(onClick = { onClose() }) { Icon(Icons.Outlined.KeyboardArrowDown, "close") }
    }
}
