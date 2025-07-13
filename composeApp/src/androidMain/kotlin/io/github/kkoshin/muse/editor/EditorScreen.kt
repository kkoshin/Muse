package io.github.kkoshin.muse.editor

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.foodiestudio.sugar.notification.toast
import io.github.kkoshin.muse.tts.Voice
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data class EditorArgs(
    val scriptId: String,
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
    onExportRequest: (List<Voice>, ExportMode) -> Unit,
    onPickVoice: () -> Unit,
) {
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    var loadingVisible by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var selectedMode by remember { mutableStateOf(ExportMode.Reading) }

    var phrases: List<String> by remember {
        mutableStateOf(emptyList())
    }

    LaunchedEffect(key1 = args) {
        viewModel.queryPhrases(args.scriptId)?.let {
            phrases = it
        } ?: context.toast("Failed loading phrases")
    }

    if (loadingVisible) {
        Dialog(
            onDismissRequest = {},
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        ) {
            CircularProgressIndicator()
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            Surface(elevation = AppBarDefaults.TopAppBarElevation) {
                Column {
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
                    )
                    ExportModeTabRow(
                        modifier = Modifier,
                        selectedMode = selectedMode
                    ) {
                        selectedMode = it
                    }
                }
            }
        },
        content = { paddingValues ->
            when (selectedMode) {
                ExportMode.Reading -> {
                    Text(
                        phrases.joinToString(" "),
                        Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                            .padding(16.dp),
                    )
                }

                ExportMode.Dictation -> {
                    FlowRow(
                        Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        phrases.forEach {
                            OutlinedButton(onClick = { /*TODO*/ }) {
                                Text(text = it)
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (phrases.isNotEmpty()) {
                FloatingActionButton(
                    backgroundColor = MaterialTheme.colors.primary,
                    shape = RoundedCornerShape(16.dp),
                    onClick = {
                        scope.launch {
                            loadingVisible = true
                            viewModel
                                .fetchAvailableVoices()
                                .onSuccess {
                                    if (it.isEmpty()) {
                                        onPickVoice()
                                    } else {
                                        onExportRequest(it, selectedMode)
                                    }
                                }.onFailure { e ->
                                    context.toast(e.message)
                                }
                            loadingVisible = false
                        }
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
            }
        },
    )
}