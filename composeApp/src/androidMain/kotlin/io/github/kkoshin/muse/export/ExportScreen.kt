package io.github.kkoshin.muse.export

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration.Companion.seconds

@Serializable
class ExportArgs(
    val voiceId: String,
    val scriptId: String,
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
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
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
                    IconButton(onClick = {
                        backPressedDispatcher?.onBackPressed()
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = null)
                    }
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
) {
    LaunchedEffect(voiceId, phrases) {
        if (phrases.isNotEmpty()) {
            viewModel.startTTS(voiceId, phrases) {
                viewModel.mixAudioAsMp3(silence, phrases, it)
            }
        }
    }

    AudioProcessingView(
        modifier,
        progress = progress,
        successLabel = "Export done!",
        onRetry = {
            when (progress) {
                is TTSFailed -> viewModel.startTTS(voiceId, phrases) {
                    viewModel.mixAudioAsMp3(silence, phrases, it)
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
