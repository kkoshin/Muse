@file:Suppress("ktlint:standard:no-wildcard-imports")

package io.github.kkoshin.muse.dashboard

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.AlertDialog
import androidx.compose.material.Checkbox
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
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
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.github.foodiestudio.sugar.notification.toast
import io.github.kkoshin.muse.R
import io.github.kkoshin.muse.debugLog
import io.github.kkoshin.muse.repo.MAX_TEXT_LENGTH
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import muse.composeapp.generated.resources.*
import muse.composeapp.generated.resources.Res
import okio.buffer
import okio.source
import okio.use
import org.jetbrains.compose.resources.stringResource
import org.koin.androidx.compose.koinViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID
import androidx.compose.ui.res.stringResource as strResource

@Serializable
object DashboardArgs

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    contentUri: String?,
    initScriptId: UUID?,
    viewModel: DashboardViewModel = koinViewModel(),
    onLaunchEditor: (Script) -> Unit,
    onCreateScriptRequest: () -> Unit,
    onLaunchSettingsPage: () -> Unit,
    onLaunchHistory: () -> Unit,
) {
    val scripts by viewModel.scripts.collectAsState()
    val context = LocalContext.current

    var importConfirmDialogVisible by remember(contentUri) {
        mutableStateOf(contentUri != null)
    }

    val scope = rememberCoroutineScope()
    val filePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                scope.launch(Dispatchers.IO) {
                    val content = readTextContent(context, uri, false)
                    if (content.length > MAX_TEXT_LENGTH) {
                        withContext(Dispatchers.Main) {
                            context.toast("Text is too long, import failed.")
                        }
                    } else {
                        viewModel.importScript(content)
                    }
                }
            }
        }

    if (importConfirmDialogVisible && contentUri != null) {
        val displayName = getFileNameFromContentResolver(context, contentUri.toUri())!!

        ImportConfirmDialog(fileName = displayName, onConfirm = { formatEnabled ->
            scope.launch(Dispatchers.IO) {
                val content = readTextContent(context, contentUri.toUri(), formatEnabled)
                if (content.length > MAX_TEXT_LENGTH) {
                    withContext(Dispatchers.Main) {
                        context.toast("Text is too long, import failed.")
                    }
                } else {
                    viewModel.importScript(content)
                }
            }
            importConfirmDialogVisible = false
        }, onCancel = {
            importConfirmDialogVisible = false
        })
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
                title = { Text(text = strResource(id = R.string.app_name)) },
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
                        append("\" to create your first script.")

                    }
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "No scripts :(", style = MaterialTheme.typography.h5)
                            Text(text = text, inlineContent = mapOf(modId to InlineTextContent(
                                placeholder = Placeholder(
                                    width = 24.sp,
                                    height = 24.sp,
                                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                                ),
                                children = {
                                    Icon(
                                        Icons.Filled.Edit,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            )))
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
                                }
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
                        filePicker.launch("text/*")
                    },
                ) {
                    Icon(Icons.Outlined.FileOpen, contentDescription = null)
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ScriptItem(
    modifier: Modifier = Modifier,
    script: Script,
    onDelete: (UUID) -> Unit
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
        }
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
                        .align(alignment)
                )
            }
        }) {
        Row(
            modifier
                .fillMaxWidth()
                .background(Color.White)
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
                Text(script.summary, maxLines = 2, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.subtitle1)
                Text(
                    text = script.createAt.formatTimeDisplay(),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.caption
                )
            }
        }
    }
}

private fun Long.formatTimeDisplay(): String {
    return Instant.ofEpochMilli(this).let {
        val time = LocalDateTime.ofInstant(it, ZoneId.systemDefault())
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(time)
    }
}

/**
 * workaround: AppFileHelper 处理对非 document uri 时有问题
 * ```Kotlin
 * AppFileHelper(context.applicationContext)
 *    .fileSystem
 *    .metadata(
 *         contentUri.toPath(),
 *    ).displayName
 * ```
 */
fun getFileNameFromContentResolver(
    context: Context,
    uri: Uri,
): String? {
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    return it.getString(nameIndex)
                }
            }
        }
    }
    return null
}

private fun readTextContent(
    context: Context,
    contentUri: Uri,
    formatEnabled: Boolean,
): String =
    context.contentResolver
        .openInputStream(contentUri)
        ?.source()
        ?.buffer()
        ?.use {
            it.readString(Charsets.UTF_8)
        }?.let {
            if (formatEnabled) {
                it.replace("\n", " ")
            } else {
                it
            }
        }
        ?: ""

@Composable
private fun ImportConfirmDialog(
    modifier: Modifier = Modifier,
    fileName: String,
    onConfirm: (Boolean) -> Unit,
    onCancel: () -> Unit,
) {
    var replaceEnabled by remember { mutableStateOf(false) }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            onCancel()
        },
        title = { Text("Confirm") },
        text = {
            Column {
                Text(stringResource(Res.string.import_file_content_with_file_name, fileName))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        stringResource(Res.string.format_replace_newlines_with_spaces),
                        modifier = Modifier.weight(1f),
                    )
                    Checkbox(replaceEnabled, onCheckedChange = {
                        replaceEnabled = it
                    })
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(replaceEnabled)
            }) {
                Text("Import")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onCancel()
            }) {
                Text("Cancel")
            }
        },
    )
}