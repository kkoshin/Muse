package io.github.kkoshin.muse.feature.export

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kkoshin.muse.feature.editor.ExportMode
import io.github.kkoshin.muse.platformbridge.AppBackButton
import io.github.kkoshin.muse.platformbridge.BackHandler
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.seconds

@Serializable
class ExportArgs(
    val voiceId: String,
    val scriptId: String,
    val exportMode: String,
    val fixedDurationEnabled: Boolean,
    val fixedSilenceSeconds: Float,
    val silencePerCharSeconds: Float,
    val minDynamicDurationSeconds: Float,
)

@Composable
fun ExportScreen(
    modifier: Modifier = Modifier,
    args: ExportArgs,
    viewModel: ExportViewModel = koinViewModel(),
    onExit: (isSuccess: Boolean) -> Unit,
) {
    val progress by viewModel.progress.collectAsState()
    val silenceDuration: SilenceDuration = if (args.fixedDurationEnabled) {
        SilenceDuration.Fixed(args.fixedSilenceSeconds.toDouble().seconds)
    } else {
        SilenceDuration.Dynamic(
            min = args.minDynamicDurationSeconds.toDouble().seconds,
            durationPerChar = args.silencePerCharSeconds.toDouble().seconds,
        )
    }

    var phrases: List<String> by remember {
        mutableStateOf(emptyList())
    }

    LaunchedEffect(key1 = args) {
        viewModel.queryPhrases(args.scriptId)?.let {
            phrases = it
        }
    }

    BackHandler {
        onExit(progress is ProgressStatus.Success)
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    AppBackButton(
                        onBack = {
                            onExit(progress is ProgressStatus.Success)
                        }
                    )
                },
                windowInsets = WindowInsets.statusBars,
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp,
            )
        },
        content = { contentPadding ->
            Box(Modifier.padding(contentPadding)) {
                Content(
                    modifier = Modifier.fillMaxSize(),
                    phrases = phrases,
                    voiceId = args.voiceId,
                    silence = silenceDuration,
                    progress,
                    viewModel = viewModel,
                    mode = ExportMode.fromName(args.exportMode)!!
                )
            }
        },
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    phrases: List<String>,
    voiceId: String,
    silence: SilenceDuration,
    progress: ProgressStatus,
    viewModel: ExportViewModel,
    mode: ExportMode,
) {
    LaunchedEffect(voiceId, phrases) {
        if (phrases.isNotEmpty()) {
            when (mode) {
                ExportMode.Reading -> viewModel.startTTSForReadingMode(
                    voiceId,
                    phrases.joinToString(" ")
                )

                ExportMode.Dictation -> viewModel.startTTSForDictationMode(voiceId, phrases) {
                    viewModel.mixAudioAsMp3(silence, phrases, it)
                }
            }
        }
    }

    AudioProcessingView(
        modifier,
        progress = progress,
        successLabel = "Export done!",
        onRetry = {
            when (progress) {
                is TTSFailed -> when (mode) {
                    ExportMode.Reading -> viewModel.startTTSForReadingMode(
                        voiceId,
                        phrases.joinToString(" ")
                    )

                    ExportMode.Dictation -> viewModel.startTTSForDictationMode(
                        voiceId,
                        phrases
                    ) {
                        viewModel.mixAudioAsMp3(silence, phrases, it)
                    }
                }

                is MixFailed -> viewModel.mixAudioAsMp3(
                    silence,
                    phrases,
                    progress.pcmList,
                )

                else -> {}
            }
        }
    )
}
