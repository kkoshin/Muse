package io.github.kkoshin.muse.feature.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.kkoshin.muse.core.manager.AccountManager
import io.github.kkoshin.muse.core.provider.Voice
import io.github.kkoshin.muse.designsystem.component.MuseCircularProgressIndicator
import io.github.kkoshin.muse.designsystem.component.MuseOutlinedButton
import io.github.kkoshin.muse.designsystem.component.MuseScaffold
import io.github.kkoshin.muse.designsystem.component.MuseTopAppBar
import io.github.kkoshin.muse.editor.ExportModeTabRow
import io.github.kkoshin.muse.feature.setting.ApiKeyRequiredSheet
import io.github.kkoshin.muse.platformbridge.AppBackButton
import io.github.kkoshin.muse.platformbridge.LocalToaster
import kotlinx.coroutines.launch
import io.github.kkoshin.muse.Route
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data class EditorArgs(
    val scriptId: String,
) : Route

/**
 * 1. show processing progress
 * 2. config silence duration
 * 3. request to export as mp3
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EditorScreen(
    args: EditorArgs,
    modifier: Modifier = Modifier,
    viewModel: EditorViewModel = koinViewModel(),
    onExportRequest: (List<Voice>, ExportMode) -> Unit,
    onPickVoice: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToApiKeyHelp: () -> Unit,
) {
    val localToaster = LocalToaster.current
    val accountManager = koinInject<AccountManager>()
    val apiKeyConfigured by accountManager.apiKeyConfigured.collectAsState(false)

    var loadingVisible by remember {
        mutableStateOf(false)
    }

    var showApiKeyDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    var selectedMode by remember { mutableStateOf(ExportMode.Reading) }

    var phrases: List<String> by remember {
        mutableStateOf(emptyList())
    }

    LaunchedEffect(key1 = args) {
        viewModel.queryPhrases(args.scriptId)?.let {
            phrases = it
        } ?: localToaster.show("Failed loading phrases")
    }

    if (loadingVisible) {
        Dialog(
            onDismissRequest = {},
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        ) {
            MuseCircularProgressIndicator()
        }
    }

    MuseScaffold(
        modifier = modifier,
        topBar = {
            Column {
                MuseTopAppBar(
                    navigationIcon = {
                        AppBackButton()
                    },
                    title = { Text(text = "Editor") },
                )
                ExportModeTabRow(
                    modifier = Modifier,
                    selectedMode = selectedMode
                ) {
                    selectedMode = it
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
                            MuseOutlinedButton(onClick = { /*TODO*/ }) {
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
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp),
                    onClick = {
                        if (!apiKeyConfigured) {
                            showApiKeyDialog = true
                        } else {
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
                                        localToaster.show(e.message)
                                    }
                                loadingVisible = false
                            }
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

    if (showApiKeyDialog) {
        ApiKeyRequiredSheet(
            onGoToSettings = {
                showApiKeyDialog = false
                onNavigateToSettings()
            },
            onWhatIsApiKey = {
                showApiKeyDialog = false
                onNavigateToApiKeyHelp()
            },
            onDismiss = { showApiKeyDialog = false },
        )
    }
}