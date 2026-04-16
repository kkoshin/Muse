package io.github.kkoshin.muse.playground.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Expand
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import io.github.kkoshin.muse.playground.core.ExportManager
import io.github.kkoshin.muse.playground.data.Caption
import io.github.kkoshin.muse.playground.data.CaptionStyle
import io.github.kkoshin.muse.playground.data.CaptionTransform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

private val DefaultCaption: Caption = Caption(
    text = "Hello World",
    style = CaptionStyle()
)

@Composable
fun CaptionView() {
    var captionTransform by remember {
        mutableStateOf(CaptionTransform(DpOffset(100.dp, 100.dp)))
    }
    val textMeasurer = rememberTextMeasurer()
    val exportManager = remember { ExportManager() }
    val scope = rememberCoroutineScope()

    Box {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCaption(captionTransform, DefaultCaption, textMeasurer)
        }

        SelectionBox(Modifier.size(200.dp, 200.dp).offset(200.dp, 100.dp))

        Button(onClick = {
            // Desktop 最佳实践：使用文件选择对话框
            val fileDialog = FileDialog(null as Frame?, "Export Caption", FileDialog.SAVE).apply {
                // 默认建议保存到用户图片目录
                val picturesDir = File(System.getProperty("user.home"), "Pictures")
                if (picturesDir.exists()) {
                    directory = picturesDir.absolutePath
                }
                file = "caption_${System.currentTimeMillis()}.png"
                isVisible = true
            }

            val selectedFile = fileDialog.file
            val selectedDir = fileDialog.directory

            if (selectedFile != null && selectedDir != null) {
                val destination = File(selectedDir, selectedFile)
                // 在协程 IO 线程中执行文件写入，避免阻塞 UI
                scope.launch(Dispatchers.IO) {
                    exportManager.exportToFile(
                        caption = DefaultCaption,
                        captionTransform = captionTransform,
                        width = 1920, // 导出更高分辨率
                        height = 1080,
                        textMeasurer = textMeasurer,
                        file = destination
                    )
                }
            }
        }, modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) {
            Text("Export")
        }
    }
}// action menu: zoom, delete
@Composable
private fun SelectionBox(modifier: Modifier = Modifier) {
    Box(
        modifier
    ) {
        Spacer(
            Modifier
                .matchParentSize()
                .border(2.dp, MaterialTheme.colors.secondary, RoundedCornerShape(16.dp))
        )
        IconButton(
            modifier = Modifier.align(Alignment.TopEnd).offset(24.dp, y = -24.dp),
            onClick = {}) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colors.onSecondary,
                modifier = Modifier.background(MaterialTheme.colors.primary, CircleShape)
                    .padding(8.dp)
            )
        }

        IconButton(
            modifier = Modifier.align(Alignment.BottomEnd).offset(24.dp, y = 24.dp),
            onClick = {}) {
            Icon(
                Icons.Default.Expand, contentDescription = "Scale",
                tint = MaterialTheme.colors.onSecondary,
                modifier = Modifier.background(MaterialTheme.colors.primary, CircleShape)
                    .padding(8.dp)
            )
        }
    }
}