package io.github.kkoshin.muse.editor

import android.net.Uri
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.kkoshin.muse.export.ExportButton
import io.github.kkoshin.muse.export.rememberAudioExportPipeline
import io.github.kkoshin.muse.tts.CharacterQuota
import io.github.kkoshin.muse.tts.Voice
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration.Companion.seconds

@Serializable
data class EditorArgs(
    val phrases: List<String>,
)

/**
 * 1. show processing progress
 * 2. config silence duration
 * 3. request to export as mp3
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditorScreen(
    modifier: Modifier = Modifier,
    args: EditorArgs,
    viewModel: EditorViewModel = koinViewModel(),
    onExportRequest: () -> Unit,
    onLaunchVoicePicker: (voiceId: String?) -> Unit,
    onExport: (pcm: List<Uri>, audio: List<Uri>) -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.refreshQuota()
    }

    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeContent,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.statusBars,
                navigationIcon = {
                    IconButton(onClick = {
                        backPressedDispatcher?.onBackPressed()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                backgroundColor = MaterialTheme.colors.surface,
                title = { Text(text = "Editor") },
                actions = {
                    IconButton(onClick = {
                        // TODO: edit phrases
                    }) {
                        Icon(Icons.Default.EditNote, null)
                    }
                },
            )
        },
        content = { paddingValues ->
            FlowRow(
                Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                args.phrases.forEach {
                    OutlinedButton(onClick = { /*TODO*/ }) {
                        Text(text = it)
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                backgroundColor = MaterialTheme.colors.primary,
                shape = RoundedCornerShape(16.dp),
                onClick = {
                    onExportRequest()
                },
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    Icon(Icons.Filled.AudioFile, contentDescription = null)
                    Text("Export")
                }
            }
        },
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    viewModel: EditorViewModel,
) {
    val progress by viewModel.progress.collectAsState()

    var selectedVoice: Voice? by remember {
        mutableStateOf(null)
    }

    Box(
        modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (progress) {
            is ProgressStatus.Idle -> {
                val quota = (progress as ProgressStatus.Idle).characterQuota
                if (quota == CharacterQuota.unknown) {
                    CircularProgressIndicator()
                } else {
                    Column(Modifier.width(IntrinsicSize.Max)) {
                        Text(
                            text = "Character Quota",
                            style = MaterialTheme.typography.h5,
                        )
                        Text(text = "Remaining: ${quota.remaining}/${quota.total}")
                        Button(onClick = {
                            // TODO:
//                            onLaunchVoicePicker(selectedVoice?.voiceId)
                        }) {
                            Text(text = selectedVoice?.name ?: "pick a voice")
                        }
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            enabled = quota.remaining > 0,
                            onClick = {
//                                viewModel.startTTS(args.phrases.map { it.lowercase() })
                            },
                        ) {
                            Text(text = "Start to TTS")
                        }
                    }
                }
            }

            is ProgressStatus.Processing -> {
                with((progress as ProgressStatus.Processing)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(Modifier.padding(end = 12.dp))
                        Column {
                            // TODO(Jiangc): 展示具体的进度
                            Text(text = "Processing...")
//                                    Text(text = "Processing phrase: $phrase")
                        }
                    }
                }
            }

            is ProgressStatus.Success -> {
                var silence by remember {
                    mutableFloatStateOf(1.0f)
                }
                val context = LocalContext.current
                val audioExportPipeline =
                    rememberAudioExportPipeline(
                        context = context,
                        input = (progress as ProgressStatus.Success).pcmList,
                        paddingSilence = silence.toInt().seconds,
                    )

                LaunchedEffect(silence) {
                    audioExportPipeline.paddingSilence = silence.toInt().seconds
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Silence: ${silence.toInt()} second(s)",
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Slider(value = silence, valueRange = 0f..5.0f, onValueChange = {
                        silence = it
                    })
                    ExportButton(modifier = Modifier, exportPipeline = audioExportPipeline)
                }
            }

            is ProgressStatus.Failed -> {
                Column {
                    Text(text = "Failed!: ${(progress as ProgressStatus.Failed).errorMsg}")
                    Button(onClick = {
//                        viewModel.startTTS(args.phrases.map { it.lowercase() })
                    }) {
                        Text(text = "Retry")
                    }
                }
            }
        }
    }
}