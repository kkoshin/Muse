package io.github.kkoshin.muse.playground.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import io.github.kkoshin.muse.playground.core.ExportManager
import io.github.kkoshin.muse.playground.data.Caption
import io.github.kkoshin.muse.playground.data.CaptionStyle
import io.github.kkoshin.muse.playground.data.CaptionTransform
import io.github.kkoshin.muse.playground.ui.components.ColorPicker
import io.github.kkoshin.muse.playground.ui.components.NumericSlider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

private val DefaultCaption: Caption = Caption(
    text = "这是一段测试文本，其中部分内容是需要高亮处理，也会包含部分换行操作等。\n比如：<b>加粗效果</b>\n部分字放大效果等等等",
    style = CaptionStyle()
)

@Composable
fun CaptionView() {
    var captionTransform by remember {
        mutableStateOf(CaptionTransform(DpOffset(100.dp, 100.dp)))
    }
    var captionStyle by remember { mutableStateOf(CaptionStyle()) }
    var isSelectionBoxVisible by remember { mutableStateOf(false) }
    val caption = remember(captionStyle) { DefaultCaption.copy(style = captionStyle) }

    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    val textLayoutResult = remember(caption, textMeasurer) {
        textMeasurer.measure(caption.text, TextStyle())
    }

    val exportManager = remember { ExportManager() }
    val scope = rememberCoroutineScope()

    Row(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .background(androidx.compose.ui.graphics.Color(0xFFF5F5F5))
                .pointerInput(Unit) {
                    detectTapGestures {
                        isSelectionBoxVisible = true
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCaption(captionTransform, caption, textMeasurer)
            }

            if (isSelectionBoxVisible) {

                val padding = captionStyle.background?.contentPadding ?: 0.dp
                val borderPadding = (captionStyle.border?.width ?: 0.dp) / 2
                val totalPadding = padding + borderPadding

                val boxWidth = with(density) {
                    (textLayoutResult.size.width.toDp() + totalPadding * 2) * captionTransform.scale
                }
                val boxHeight = with(density) {
                    (textLayoutResult.size.height.toDp() + totalPadding * 2) * captionTransform.scale
                }
                val boxOffset = DpOffset(
                    captionTransform.offset.x - totalPadding * captionTransform.scale,
                    captionTransform.offset.y - totalPadding * captionTransform.scale
                )

                SelectionBox(
                    modifier = Modifier
                        .size(boxWidth, boxHeight)
                        .offset(boxOffset.x, boxOffset.y)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                captionTransform = captionTransform.copy(
                                    offset = DpOffset(
                                        captionTransform.offset.x + dragAmount.x.toDp(),
                                        captionTransform.offset.y + dragAmount.y.toDp()
                                    )
                                )
                            }
                        },
                    onClose = { isSelectionBoxVisible = false },
                    onScaleDrag = { dragAmount ->
                        val oldScale = captionTransform.scale
                        
                        // 计算当前 SelectionBox 的中心和尺寸
                        val padding = captionStyle.background?.contentPadding ?: 0.dp
                        val borderPadding = (captionStyle.border?.width ?: 0.dp) / 2
                        val totalPadding = padding + borderPadding
                        
                        val textSizeDp = with(density) {
                            DpOffset(textLayoutResult.size.width.toDp(), textLayoutResult.size.height.toDp())
                        }
                        
                        val boxWidth = (textSizeDp.x + totalPadding * 2) * oldScale
                        val boxHeight = (textSizeDp.y + totalPadding * 2) * oldScale
                        
                        // 当前 handle 相对于中心的距离 (初始距离)
                        val d1 = Math.sqrt(Math.pow(boxWidth.value.toDouble() / 2, 2.0) + Math.pow(boxHeight.value.toDouble() / 2, 2.0))
                        
                        // 拖动后的 handle 相对于中心的距离
                        // dragAmount 是相对于 handle 的位移，handle 位于中心 + (w/2, h/2)
                        val dragAmountDpX = with(density) { dragAmount.x.toDp() }
                        val dragAmountDpY = with(density) { dragAmount.y.toDp() }
                        val nextHandleX = boxWidth.value / 2 + dragAmountDpX.value
                        val nextHandleY = boxHeight.value / 2 + dragAmountDpY.value
                        val d2 = Math.sqrt(Math.pow(nextHandleX.toDouble(), 2.0) + Math.pow(nextHandleY.toDouble(), 2.0))
                        
                        val scaleRatio = (d2 / d1).toFloat()
                        val newScale = (oldScale * scaleRatio).coerceAtLeast(0.1f)
                        
                        // 为了保持中心不变：newOffset = oldOffset + (textSize / 2) * (oldScale - newScale)
                        val newOffset = DpOffset(
                            captionTransform.offset.x + (textSizeDp.x / 2) * (oldScale - newScale),
                            captionTransform.offset.y + (textSizeDp.y / 2) * (oldScale - newScale)
                        )
                        
                        captionTransform = captionTransform.copy(
                            offset = newOffset,
                            scale = newScale
                        )
                    }
                )
            }

            Button(
                onClick = {
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
                                caption = caption,
                                captionTransform = captionTransform,
                                width = 1920, // 导出更高分辨率
                                height = 1080,
                                textMeasurer = textMeasurer,
                                file = destination
                            )
                        }
                    }
                },
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
            ) {
                Text("Export")
            }
        }

        // Sidebar
        Column(
            modifier = Modifier
                .width(300.dp)
                .fillMaxHeight()
                .background(Color.LightGray)
                .border(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.2f))
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Style Sidebar",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.h6
            )

            // Text Color
            Text(
                "Text Color",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.subtitle2
            )
            ColorPicker(
                selectedColor = captionStyle.textColor,
                onColorSelected = { captionStyle = captionStyle.copy(textColor = it) },
                modifier = Modifier.padding(16.dp)
            )

            Divider()

            // Background
            Text(
                "Background",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.subtitle2
            )

            val hasBackground = captionStyle.background != null
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Checkbox(checked = hasBackground, onCheckedChange = {
                    captionStyle = if (it) {
                        captionStyle.copy(
                            background = CaptionStyle.Background(
                                8.dp,
                                8.dp,
                                java.awt.Color.LIGHT_GRAY
                            )
                        )
                    } else {
                        captionStyle.copy(background = null)
                    }
                })
                Text("Enable Background")
            }

            if (hasBackground) {
                val bg = captionStyle.background!!
                ColorPicker(
                    selectedColor = bg.color,
                    onColorSelected = { captionStyle = captionStyle.copy(background = bg.copy(color = it)) },
                    modifier = Modifier.padding(16.dp)
                )
                NumericSlider(
                    label = "Padding",
                    value = bg.contentPadding.value,
                    onValueChange = {
                        captionStyle = captionStyle.copy(background = bg.copy(contentPadding = it.dp))
                    },
                    valueRange = 0f..50f,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                NumericSlider(
                    label = "Radius",
                    value = bg.radius.value,
                    onValueChange = {
                        captionStyle = captionStyle.copy(background = bg.copy(radius = it.dp))
                    },
                    valueRange = 0f..50f,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Divider()

            // Border
            Text(
                "Border",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.subtitle2
            )
            val hasBorder = captionStyle.border != null
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Checkbox(checked = hasBorder, onCheckedChange = {
                    captionStyle = if (it) {
                        captionStyle.copy(border = CaptionStyle.Border(java.awt.Color.BLACK, 2.dp))
                    } else {
                        captionStyle.copy(border = null)
                    }
                })
                Text("Enable Border")
            }

            if (hasBorder) {
                val border = captionStyle.border!!
                ColorPicker(
                    selectedColor = border.color,
                    onColorSelected = {
                        captionStyle = captionStyle.copy(border = border.copy(color = it))
                    },
                    modifier = Modifier.padding(16.dp)
                )
                NumericSlider(
                    label = "Width",
                    value = border.width.value,
                    onValueChange = {
                        captionStyle = captionStyle.copy(border = border.copy(width = it.dp))
                    },
                    valueRange = 0f..20f,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// action menu: zoom, delete
@Composable
private fun SelectionBox(
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {},
    onScaleDrag: (androidx.compose.ui.geometry.Offset) -> Unit = {}
) {
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
            onClick = onClose) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colors.onSecondary,
                modifier = Modifier.background(MaterialTheme.colors.primary, CircleShape)
                    .padding(8.dp)
            )
        }

        IconButton(
            modifier = Modifier.align(Alignment.BottomEnd).offset(24.dp, y = 24.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        onScaleDrag(dragAmount)
                    }
                },
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
