package io.github.kkoshin.muse.app.diagnosis

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.github.foodiestudio.sugar.notification.toast
import io.github.kkoshin.muse.feature.dashboard.formatTimeDisplay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

private const val MAX_CRASH_LOGS = 10

@Composable
fun CrashLogScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val tombstonesDir = File(context.filesDir, "tombstones")
    var files by remember {
        mutableStateOf(
            if (tombstonesDir.exists()) {
                (tombstonesDir.listFiles()?.toList()
                    ?: emptyList()).sortedByDescending { it.lastModified() }.take(MAX_CRASH_LOGS)
            } else {
                emptyList()
            }
        )
    }
    val scope = rememberCoroutineScope()
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            TopAppBar(
                title = { Text("Recent Crashes", color = MaterialTheme.colors.onError) },
                windowInsets = WindowInsets.statusBars,
                backgroundColor = MaterialTheme.colors.error,
                actions = {
                    var showDialog by remember { mutableStateOf(false) }
                    val enabled = files.isNotEmpty()
                    IconButton(
                        onClick = { showDialog = true },
                        enabled = enabled
                    ) {
                        Icon(
                            Icons.Default.DeleteSweep,
                            contentDescription = "Clear All",
                            tint = MaterialTheme.colors.onError.copy(alpha = if (enabled) 1f else 0.5f)
                        )
                    }
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = { Text("Clear All Logs") },
                            text = { Text("Are you sure you want to delete all crash logs?") },
                            confirmButton = {
                                TextButton(onClick = {
                                    scope.launch(Dispatchers.IO) {
                                        val success = tombstonesDir.deleteRecursively()
                                        if (!success) {
                                            context.toast("Failed to delete crash logs.")
                                        } else {
                                            files = emptyList()
                                        }
                                    }
                                    showDialog = false
                                }) {
                                    Text("Confirm")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDialog = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (files.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        modifier = Modifier.size(148.dp),
                        imageVector = Icons.Default.Celebration,
                        contentDescription = null,
                        tint = MaterialTheme.colors.onSurface.copy(alpha = 0.85f)
                    )
                    Text(text = "No crash logs")
                }
            }
        } else {
            LazyColumn(
                contentPadding = paddingValues
            ) {
                items(files) { file ->
                    CrashLogItem(file = file)
                }
                if (files.size > MAX_CRASH_LOGS) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Showing last $MAX_CRASH_LOGS records only.",
                                style = MaterialTheme.typography.caption,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CrashLogItem(modifier: Modifier = Modifier, file: File) {
    // 其中 Crash 的名称 tombstone_{timestamp}_{versionCode}_{packageName}.java.xcrash
    val context = LocalContext.current
    val displayName = runCatching {
        file.name.split("_").let {
            "${it[1]}-${it[2]}"
        }
    }.getOrElse { file.name }
    ListItem(
        modifier = modifier
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW)
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                intent.setDataAndType(uri, "text/plain")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                context.startActivity(intent)
            }
            .padding(vertical = 8.dp),
        text = {
            Text(
                displayName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        secondaryText = {
            Text(
                file.lastModified().formatTimeDisplay(),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
            )
        },
        trailing = {
            IconButton(onClick = {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        file
                    )
                    type = "text/plain"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(intent, "Share Crash Log"))
            }) {
                Icon(Icons.Default.Share, "share")
            }
        },
    )
}
