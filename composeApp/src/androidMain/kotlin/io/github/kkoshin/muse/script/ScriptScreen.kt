@file:Suppress("ktlint:standard:no-wildcard-imports")

package io.github.kkoshin.muse.script

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Checkbox
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.github.foodiestudio.sugar.ExperimentalSugarApi
import io.github.kkoshin.muse.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import muse.composeapp.generated.resources.*
import muse.composeapp.generated.resources.Res
import okio.buffer
import okio.source
import okio.use
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.res.stringResource as strResource

@Serializable
object ScriptArgs

/**
 * 1. input/paste sentences here.
 * 2. convert to phrases.
 * 3. request to tts.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalSugarApi::class)
@Composable
fun ScriptScreen(
    modifier: Modifier = Modifier,
    contentUri: String? = null,
    onRequest: (phrases: List<String>) -> Unit,
    onLaunchSettingsPage: () -> Unit,
) {
    var script: String by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    val phrases: List<String> by remember {
        derivedStateOf {
            script.split(" ", "\n").filterNot { it.isBlank() }
        }
    }

    var importConfirmDialogVisible by remember(contentUri) {
        mutableStateOf(contentUri != null)
    }

    val scope = rememberCoroutineScope()

    if (importConfirmDialogVisible && contentUri != null) {
        val displayName = getFileNameFromContentResolver(context, contentUri.toUri())!!

        ImportConfirmDialog(fileName = displayName, onConfirm = { formatEnabled ->
            scope.launch(Dispatchers.IO) {
                script = readTextContent(context, contentUri, formatEnabled)
            }
            importConfirmDialogVisible = false
        }, onCancel = {
            importConfirmDialogVisible = false
        })
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeContent,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.statusBars,
                title = { Text(text = strResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = { onLaunchSettingsPage() }) {
                        Icon(Icons.Default.Settings, "settings")
                    }
                },
            )
        },
        content = {
            Column(Modifier.padding(it)) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    value = script,
                    minLines = 3,
                    maxLines = 5,
                    onValueChange = {
                        script = it
                    },
                    placeholder = {
                        Text("Input your script here")
                    },
                )
                Text(text = "Phrases:", Modifier.padding(horizontal = 16.dp))
                FlowRow(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
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
        },
        floatingActionButton = {
            if (phrases.isNotEmpty()) {
                FloatingActionButton(onClick = { onRequest(phrases) }) {
                    Icon(Icons.Filled.ArrowForward, contentDescription = null)
                }
            }
        },
    )
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
    contentUri: String,
    formatEnabled: Boolean,
): String =
    context.contentResolver
        .openInputStream(contentUri.toUri())
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