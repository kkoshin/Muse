@file:Suppress("ktlint:standard:no-wildcard-imports")
@file:OptIn(ExperimentalUuidApi::class)

package io.github.kkoshin.muse.feature.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.MusicOff
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kkoshin.muse.platformbridge.MimeType
import io.github.kkoshin.muse.platformbridge.rememberDocumentPicker
import io.github.kkoshin.muse.repo.model.Script
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import museroot.muse.generated.resources.Res
import museroot.muse.generated.resources.projects
import okio.Path
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
object DashboardArgs

@OptIn(ExperimentalUuidApi::class)
@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    initScriptId: Uuid?,
    viewModel: DashboardViewModel = koinViewModel(),
    onLaunchEditor: (Script) -> Unit,
    onCreateScriptRequest: () -> Unit,
    onLaunchSettingsPage: () -> Unit,
    onLaunchAudioIsolation: (uri: Path) -> Unit,
    onLaunchWhiteNoise: () -> Unit,
) {
    val scripts by viewModel.scripts.collectAsState()

    val filePicker = rememberDocumentPicker(MimeType.Audio) { path ->
        if (path != null) {
            onLaunchAudioIsolation(path)
        }
    }

    LaunchedEffect(initScriptId) {
        viewModel.loadScripts()
    }

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
                    LazyColumn(
                        contentPadding = PaddingValues(top = 8.dp, bottom = 56.dp),
                        reverseLayout = true,
                    ) {
                        items(scripts, key = { item -> item.id }) { script ->
                            ScriptItem(
                                modifier = Modifier.clickable {
                                    onLaunchEditor(script)
                                },
                                script = script,
                                onDelete = {
                                    viewModel.deleteScript(it)
                                },
                            )
                        }
                    }
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
                        filePicker.launch()
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

@OptIn(ExperimentalMaterialApi::class, ExperimentalUuidApi::class)
@Composable
private fun ScriptItem(
    modifier: Modifier = Modifier,
    script: Script,
    onDelete: (Uuid) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToEnd || it == DismissValue.DismissedToStart) {
                scope.launch {
                    delay(300)
                    onDelete(script.id)
                }
            }
            true
        },
    )

    SwipeToDismiss(
        state = dismissState,
        dismissThresholds = { FractionalThreshold(0.33f) },
        background = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss

            val scale by animateFloatAsState(targetValue = if (dismissState.targetValue == DismissValue.Default) 0.8f else 1.2f)

            val alignment = when (direction) {
                DismissDirection.EndToStart -> Alignment.CenterEnd
                DismissDirection.StartToEnd -> Alignment.CenterStart
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.error.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp),
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onError,
                    modifier = Modifier
                        .scale(scale)
                        .align(alignment),
                )
            }
        },
    ) {
        Row(
            modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background)
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Article,
                contentDescription = null,
                Modifier
                    .size(48.dp),
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    script.summary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.subtitle1
                )
                Text(
                    text = script.createAt.formatTimeDisplay(),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.caption,
                )
            }
        }
    }
}

fun Long.formatTimeDisplay(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    return "${localDateTime.year}-" +
            "${localDateTime.monthNumber.toString().padStart(2, '0')}-" +
            "${localDateTime.dayOfMonth.toString().padStart(2, '0')} " +
            "${localDateTime.hour.toString().padStart(2, '0')}:" +
            "${localDateTime.minute.toString().padStart(2, '0')}:" +
            localDateTime.second.toString().padStart(2, '0')
}