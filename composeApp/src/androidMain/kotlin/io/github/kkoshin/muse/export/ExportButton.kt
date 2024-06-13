package io.github.kkoshin.muse.export

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.documentfile.provider.DocumentFile
import com.github.foodiestudio.sugar.notification.toast
import kotlinx.coroutines.launch

@Composable
internal fun ExportButton(
    modifier: Modifier,
    exportPipeline: ExportPipeline<*>,
) {
    val context = LocalContext.current

    val isAudio = exportPipeline is AudioExportPipeline

    var targetDocument: DocumentFile? by remember {
        mutableStateOf(null)
    }

    val progress by exportPipeline.progress.collectAsState(-1)

    val transforming by remember {
        derivedStateOf {
            progress in 0..99
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            targetDocument?.delete()
        }
    }

    val scope = rememberCoroutineScope()

    fun doExport() {
        scope.launch {
            exportPipeline.start(targetDocument!!.uri)
                .onFailure {
                    it.printStackTrace()
                    context.toast(it.message)
                }
                .onSuccess {
                    targetDocument = null
                    context.toast("Export Success.")
                }
        }
    }

    val createOutputFileLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(if (isAudio) "audio/mp3" else "video/mp4")) {
            it?.let { uri ->
                targetDocument = DocumentFile.fromSingleUri(context, uri)
                doExport()
            }
        }

    if (transforming) {
        LoadingDialog("$progress% processing", progress = progress / 100f) {
            exportPipeline.cancel()
        }
    }

    Button(
        modifier = modifier,
        onClick = {
            if (targetDocument == null) {
                createOutputFileLauncher.launch(if (isAudio) "output.mp3" else "output.mp4")
            } else {
                doExport()
            }
        },
        shape = RoundedCornerShape(50),
    ) {
        Text(
            if (isAudio) {
                "Export as audio"
            } else {
                "Export as video"
            },
        )
    }
}

@Composable
private fun LoadingDialog(
    title: String?,
    progress: Float,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
        ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color.DarkGray, shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 32.dp, vertical = if (title == null) 32.dp else 24.dp),
        ) {
            CircularProgressIndicator(
                color = Color.Cyan,
                strokeWidth = 2.dp,
                progress = progress,
            )
            if (title != null) {
                Text(
                    text = title,
                    maxLines = 1,
                    color = Color.LightGray,
                    modifier = Modifier.padding(top = 16.dp),
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}