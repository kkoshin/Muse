package io.github.kkoshin.muse.editor

import android.net.Uri
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
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
//    LaunchedEffect(Unit) {
//        viewModel.refreshQuota()
//    }

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