package io.github.kkoshin.muse.feature.setting.voice

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import top.yukonga.miuix.kmp.basic.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.kkoshin.muse.audio.ui.AudioPlaybackButton
import io.github.kkoshin.muse.core.manager.SpeechProcessorManager
import io.github.kkoshin.muse.core.provider.Voice
import androidx.compose.ui.input.nestedscroll.nestedScroll
import io.github.kkoshin.muse.designsystem.component.ScreenScaffold
import io.github.kkoshin.muse.designsystem.theme.AppTheme
import io.github.kkoshin.muse.platformbridge.AppBackButton
import io.github.kkoshin.muse.platformbridge.BackHandler
import io.github.kkoshin.muse.platformbridge.LocalToaster
import io.github.kkoshin.muse.platformbridge.NavigationBarContrastEnforcedOnAndroid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import museroot.muse.generated.resources.Res
import museroot.muse.generated.resources.voices
import okio.Path.Companion.toPath
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import top.yukonga.miuix.kmp.icon.extended.SelectAll

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

    val speechProcessorManager = koinInject<SpeechProcessorManager>()
    val toaster = LocalToaster.current
    val scope = rememberCoroutineScope()

    var previewVoice: Voice? by remember {
        mutableStateOf(null)
    }
    var playbackBarVisible by remember { mutableStateOf(false) }

    BackHandler {
        scope.launch {
            speechProcessorManager.updateAvailableVoice(selected.toSet())
            onBack()
        }
    }

    NavigationBarContrastEnforcedOnAndroid(!playbackBarVisible, false)

    LaunchedEffect(Unit) {
        speechProcessorManager
            .queryVoiceList(true)
            .onSuccess {
                voices = it
            }.onFailure {
                withContext(Dispatchers.Main) {
                    toaster.show(it.message ?: "unknown error")
                }
            }
    }
    ScreenScaffold(
        modifier = modifier,
        title = stringResource(Res.string.voices),
        navigationIcon = {
            AppBackButton(
                onBack = {
                    scope.launch {
                        speechProcessorManager.updateAvailableVoice(selected.toSet())
                    }
                },
            )
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
                Icon(MiuixIcons.SelectAll, "select All")
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
                                AppTheme.colorScheme.surfaceContainerHigh,
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
    ) { paddingValues, scrollBehavior ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.nestedScroll(scrollBehavior!!.nestedScrollConnection),
        ) {
            voices
                .groupBy { it.accent }
                .forEach { (accent, voicesList) ->
                    item {
                        Text(
                            accent.name,
                            style = AppTheme.textStyles.subtitle,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    AppTheme.colorScheme.onBackground.copy(alpha = 0.08f)
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
    }
}

@Composable
private fun VoiceItem(
    voice: Voice,
    selectedVoiceIds: Set<String>,
    onSelected: (Voice, selected: Boolean) -> Unit,
    onClick: (Voice) -> Unit,
) {
    Row(
        modifier = Modifier.clickable {
            onClick(voice)
        }.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = getAccentFlag(voice.accent) + " " + voice.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Column {
                listOfNotNull(voice.gender?.raw, voice.age?.raw, voice.descriptive)
                    .joinToString("・")
                    .let {
                        Text(
                            it,
                            style = AppTheme.textStyles.footnote1,
                            color = AppTheme.colorScheme.onBackgroundVariant,
                        )
                    }
            }
        }
        Checkbox(
            state = if (selectedVoiceIds.contains(voice.voiceId)) ToggleableState.On else ToggleableState.Off,
            onClick = {
                onSelected(voice, !selectedVoiceIds.contains(voice.voiceId))
            })
    }
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
        Text(getAccentFlag(voice.accent), style = AppTheme.textStyles.title2)
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(voice.name, style = AppTheme.textStyles.body1)
            listOfNotNull(voice.gender?.raw, voice.age?.raw, voice.descriptive)
                .joinToString("・")
                .let { Text(it, style = AppTheme.textStyles.footnote1) }
        }
        AudioPlaybackButton(audioSource = voice.previewUrl.toPath())
        IconButton(onClick = { onClose() }) { Icon(Icons.Outlined.KeyboardArrowDown, "close") }
    }
}
