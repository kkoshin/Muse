@file:OptIn(ExperimentalUuidApi::class)

package io.github.kkoshin.muse.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.MusicOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kkoshin.muse.repo.model.Script
import muse.feature.generated.resources.Res
import muse.feature.generated.resources.projects
import okio.Path
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
actual fun DashboardScreen(
    modifier: Modifier,
    contentUri: Path?,
    initScriptId: Uuid?,
    viewModel: DashboardViewModel,
    onLaunchEditor: (Script) -> Unit,
    onCreateScriptRequest: () -> Unit,
    onLaunchSettingsPage: () -> Unit,
    onDeepLinkHandled: () -> Unit,
    onLaunchAudioIsolation: (uri: String) -> Unit,
    onLaunchWhiteNoise: () -> Unit
) {

    val scripts by viewModel.scripts.collectAsState()

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.statusBars,
                title = { Text(text = stringResource(Res.string.projects)) },
                backgroundColor = MaterialTheme.colors.surface,
                actions = {
//                    IconButton(onClick = {
//                        onLaunchHistory()
//                    }) {
//                        Icon(Icons.Default.History, "history")
//                    }
                    IconButton(onClick = { onLaunchSettingsPage() }) {
                        Icon(Icons.Default.Settings, "settings")
                    }
                },
            )
        },
        content = {
            Column(Modifier.padding(it)) {
                if (scripts.isEmpty()) {
                    val modId = "modIcon"
                    val text = buildAnnotatedString {
                        append("Tap \"")
                        appendInlineContent(modId, "[icon]")
                        append("\" to create your first project.")
                    }
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(text = "No projects :(", style = MaterialTheme.typography.h5)
                            Text(
                                text = text,
                                inlineContent = mapOf(
                                    modId to InlineTextContent(
                                        placeholder = Placeholder(
                                            width = 24.sp,
                                            height = 24.sp,
                                            placeholderVerticalAlign = PlaceholderVerticalAlign.Center,
                                        ),
                                        children = {
                                            Icon(
                                                Icons.Filled.Edit,
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp),
                                            )
                                        },
                                    ),
                                ),
                            )
                        }
                    }
                } else {
//                    LazyColumn(
//                        contentPadding = PaddingValues(top = 8.dp, bottom = 56.dp),
//                        reverseLayout = true,
//                    ) {
//                        items(scripts, key = { item -> item.id }) { script ->
//                            ScriptItem(
//                                modifier = Modifier.clickable {
//                                    onLaunchEditor(script)
//                                },
//                                script = script,
//                                onDelete = {
//                                    viewModel.deleteScript(it)
//                                },
//                            )
//                        }
//                    }
                }
            }
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                FloatingActionButton(
                    modifier = Modifier.size(40.dp),
                    backgroundColor = MaterialTheme.colors.background,
                    onClick = {
                        onLaunchWhiteNoise()
                    },
                ) {
                    Icon(Icons.Outlined.Campaign, contentDescription = null)
                }

                FloatingActionButton(
                    modifier = Modifier.size(40.dp),
                    backgroundColor = MaterialTheme.colors.background,
                    onClick = {
//                        filePicker.launch("audio/*")
                    },
                ) {
                    Icon(Icons.Outlined.MusicOff, contentDescription = null)
                }
                FloatingActionButton(
                    backgroundColor = MaterialTheme.colors.primary,
                    onClick = {
                        onCreateScriptRequest()
                    },
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = null)
                }
            }
        },
    )
}