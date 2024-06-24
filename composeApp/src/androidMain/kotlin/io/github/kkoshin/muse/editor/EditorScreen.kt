package io.github.kkoshin.muse.editor

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.kkoshin.muse.export.ExportButton
import io.github.kkoshin.muse.export.rememberAudioExportPipeline
import io.github.kkoshin.muse.tts.CharacterQuota
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
@Composable
fun EditorScreen(
    modifier: Modifier = Modifier,
    args: EditorArgs,
    viewModel: EditorViewModel = koinViewModel(),
    onExport: (pcm: List<Uri>, audio: List<Uri>) -> Unit,
) {
    val progress by viewModel.progress.collectAsState()
    val quota by viewModel.quota.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refreshQuota()
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeContent,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.statusBars,
                title = { Text(text = "Editor") },
            )
        },
        content = { paddingValues ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                when (progress) {
                    is ProgressStatus.Idle -> {
                        if (quota == CharacterQuota.unknown) {
                            CircularProgressIndicator()
                        } else {
                            Column(Modifier.width(IntrinsicSize.Max)) {
                                Text(
                                    text = "Character Quota",
                                    style = MaterialTheme.typography.h5,
                                )
                                Text(text = "Remaining: ${quota.remaining}/${quota.total}")
                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = quota.remaining > 0,
                                    onClick = {
                                        viewModel.startTTS(args.phrases.map { it.lowercase() })
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
                                viewModel.startTTS(args.phrases.map { it.lowercase() })
                            }) {
                                Text(text = "Retry")
                            }
                        }
                    }
                }
            }
        },
    )
}