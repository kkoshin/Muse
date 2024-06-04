package com.github.kkoshin.speaker.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.foodiestudio.sugar.ExperimentalSugarApi
import com.github.foodiestudio.sugar.storage.AppFileHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import okio.buffer
import okio.sink
import okio.source
import org.koin.androidx.compose.koinViewModel

@Serializable
data class EditorArgs(
    val phrases: List<String>,
)

/**
 * 1. show processing progress
 * 2. config silence duration
 * 3. request to export as mp3
 */
@OptIn(ExperimentalSugarApi::class)
@Composable
fun EditorScreen(
    modifier: Modifier = Modifier,
    args: EditorArgs,
    viewModel: EditorViewModel = koinViewModel(),
    onExport: () -> Unit,
) {
    val progress by viewModel.progress.collectAsState()

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeContent,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.statusBars,
                title = { Text(text = "Editor") },
                actions = {
                    IconButton(
                        enabled = progress is ProgressStatus.Success,
                        onClick = {
                            onExport()
                        },
                    ) {
                        Icon(Icons.Filled.Done, contentDescription = null)
                    }
                },
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
                        Button(onClick = {
                            viewModel.startTTS(args.phrases)
                        }) {
                            Text(text = "Start")
                        }
                    }

                    is ProgressStatus.Processing -> {
                        with((progress as ProgressStatus.Processing)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(Modifier.padding(end = 12.dp))
                                Column {
                                    Text(text = "Processing ${value}%")
                                    Text(text = "Processing phrase: $phrase")
                                }
                            }
                        }
                    }

                    is ProgressStatus.Success -> {
                        Text(text = "Completed: ${(progress as ProgressStatus.Success).audio}")
                    }

                    is ProgressStatus.Failed -> {
                        Column {
                            Text(text = "Failed!: ${(progress as ProgressStatus.Failed).errorMsg}")
                            Button(onClick = { /*TODO*/ }) {
                                Text(text = "Retry")
                            }
                        }
                    }
                }
            }
        })
}