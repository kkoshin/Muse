package io.github.kkoshin.muse.playground.ui

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import io.github.kkoshin.fancy.config.FancyConfig
import io.github.kkoshin.fancy.data.Caption
import io.github.kkoshin.fancy.data.CaptionProcessor
import io.github.kkoshin.fancy.data.CaptionStyle
import io.github.kkoshin.fancy.data.CaptionTransform
import io.github.kkoshin.fancy.data.toAnnotatedString
import io.github.kkoshin.fancy.data.toOffset
import io.github.kkoshin.fancy.data.toRelative
import io.github.kkoshin.fancy.data.toTextStyle
import io.github.kkoshin.fancy.ui.FancyCaptionView
import io.github.kkoshin.muse.playground.Constants
import io.github.kkoshin.muse.playground.core.ExportManager
import io.github.kkoshin.muse.playground.ui.components.ColorPicker
import io.github.kkoshin.muse.playground.ui.components.DebugInfo
import io.github.kkoshin.muse.playground.ui.components.NumericSlider
import io.github.kkoshin.muse.playground.ui.components.SelectionBox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

private val DefaultCaption: Caption = Caption(
    text = "这是一段测试文本，其中部分内容是需要高亮处理，也会包含部分换行操作等。\n比如：加粗效果\n部分字放大效果等等等",
    style = CaptionStyle()
)

@Composable
fun CaptionView() {
    val config = remember {
        FancyConfig(
            referenceWidth = Constants.REFERENCE_WIDTH,
            referenceHeight = Constants.REFERENCE_HEIGHT,
            referenceFontSize = Constants.REFERENCE_FONT_SIZE.toFloat()
        )
    }

    var captionTransform by remember {
        mutableStateOf(CaptionTransform())
    }
    var captionStyle by remember { mutableStateOf(CaptionStyle()) }
    var isSelectionBoxVisible by remember { mutableStateOf(false) }
    val caption = remember(captionStyle) {
        DefaultCaption.copy(
            style = captionStyle,
            segments = CaptionProcessor.processHighlight(DefaultCaption.text, captionStyle)
        )
    }

    val textMeasurer = rememberTextMeasurer()
    // 当前可能是 x2 的倍率
    val density = LocalDensity.current

    val textLayoutResult = remember(caption, textMeasurer, config) {
        val annotatedString = if (caption.segments.isNotEmpty()) {
            caption.segments.toAnnotatedString(config, caption.style)
        } else {
            androidx.compose.ui.text.AnnotatedString(caption.text)
        }
        textMeasurer.measure(annotatedString, caption.style.toTextStyle(config))
    }

    var containerSize by remember { mutableStateOf(Size.Zero) }

    val exportManager = remember { ExportManager() }
    val scope = rememberCoroutineScope()
    Row(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFFF5F5F5))
                .onGloballyPositioned {
                    containerSize = it.size.toSize()
                }
                .pointerInput(Unit) {
                    detectTapGestures {
                        isSelectionBoxVisible = true
                    }
                }
        ) {
            FancyCaptionView(
                caption = caption,
                captionTransform = captionTransform,
                config = config,
                modifier = Modifier.fillMaxSize()
            )

            if (isSelectionBoxVisible && containerSize != Size.Zero) {
                val previewScale = (containerSize.width / density.density) / config.referenceWidth
                val padding = captionStyle.background?.contentPadding ?: 0.dp
                val borderPadding = (captionStyle.border?.width ?: 0.dp) / 2
                val totalPadding = padding + borderPadding

                val boxWidth = with(density) {
                    (textLayoutResult.size.width.toDp() + totalPadding * 2) * captionTransform.scale * previewScale
                }
                val boxHeight = with(density) {
                    (textLayoutResult.size.height.toDp() + totalPadding * 2) * captionTransform.scale * previewScale
                }

                val centerPixelOffset = captionTransform.offset.toOffset(containerSize)

                val boxOffsetPx = Offset(
                    centerPixelOffset.x - with(density) { boxWidth.toPx() } / 2,
                    centerPixelOffset.y - with(density) { boxHeight.toPx() } / 2
                )

                val boxOffsetDp = with(density) {
                    androidx.compose.ui.unit.DpOffset(boxOffsetPx.x.toDp(), boxOffsetPx.y.toDp())
                }

                SelectionBox(
                    modifier = Modifier
                        .size(boxWidth, boxHeight)
                        .offset(boxOffsetDp.x, boxOffsetDp.y)
                        .pointerInput(containerSize) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val currentPixelOffset = captionTransform.offset.toOffset(containerSize)
                                val nextPixelOffset = currentPixelOffset + dragAmount
                                captionTransform = captionTransform.copy(
                                    offset = nextPixelOffset.toRelative(containerSize)
                                )
                            }
                        },
                    onClose = { isSelectionBoxVisible = false },
                    onScaleDrag = { dragAmount ->
                        val oldScale = captionTransform.scale
                        val previewScaleDrag = (containerSize.width / density.density) / config.referenceWidth

                        val textSizeDp = with(density) {
                            androidx.compose.ui.unit.DpOffset(
                                textLayoutResult.size.width.toDp(),
                                textLayoutResult.size.height.toDp()
                            )
                        }

                        val boxWidthNow = (textSizeDp.x + totalPadding * 2) * oldScale * previewScaleDrag
                        val boxHeightNow = (textSizeDp.y + totalPadding * 2) * oldScale * previewScaleDrag

                        val d1 = Math.sqrt(
                            Math.pow(
                                boxWidthNow.value.toDouble() / 2,
                                2.0
                            ) + Math.pow(boxHeightNow.value.toDouble() / 2, 2.0)
                        )

                        val dragAmountDpX = with(density) { dragAmount.x.toDp() }
                        val dragAmountDpY = with(density) { dragAmount.y.toDp() }
                        val nextHandleX = boxWidthNow.value / 2 + dragAmountDpX.value
                        val nextHandleY = boxHeightNow.value / 2 + dragAmountDpY.value
                        val d2 =
                            Math.sqrt(Math.pow(nextHandleX.toDouble(), 2.0) + Math.pow(nextHandleY.toDouble(), 2.0))

                        val scaleRatio = (d2 / d1).toFloat()
                        val newScale = (oldScale * scaleRatio).coerceAtLeast(0.1f)

                        // 中心点保持不变，直接更新缩放即可 (因为 RelativeOffset 就是中心)
                        captionTransform = captionTransform.copy(
                            scale = newScale
                        )
                    }
                )
            }

            Button(
                onClick = {
                    val fileDialog = FileDialog(null as Frame?, "Export Caption", FileDialog.SAVE).apply {
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
                        scope.launch(Dispatchers.IO) {
                            exportManager.exportToFile(
                                caption = caption,
                                captionTransform = captionTransform,
                                width = config.referenceWidth.toInt(),
                                height = config.referenceHeight.toInt(),
                                file = destination
                            )
                        }
                    }
                },
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
            ) {
                Text("Export")
            }
            DebugInfo(modifier = Modifier.align(Alignment.BottomStart), captionTransform)
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

            NumericSlider(
                label = "Font Scale",
                value = captionStyle.fontScale,
                onValueChange = {
                    captionStyle = captionStyle.copy(fontScale = it)
                },
                valueRange = 0.5f..3.0f,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            NumericSlider(
                label = "Letter Spacing",
                value = captionStyle.letterSpacing,
                onValueChange = {
                    captionStyle = captionStyle.copy(letterSpacing = it)
                },
                valueRange = 0.0f..1.0f,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Text(
                "Text Style",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.subtitle2
            )

            Row(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
                CaptionStyle.TextStyleOption.values().forEach { option ->
                    Button(
                        onClick = { captionStyle = captionStyle.copy(textStyle = option) },
                        modifier = Modifier.padding(end = 4.dp).weight(1f),
                        colors = if (captionStyle.textStyle == option) {
                            ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                        } else {
                            ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface)
                        },
                        elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp)
                    ) {
                        Text(
                            option.name,
                            style = MaterialTheme.typography.caption,
                            color = if (captionStyle.textStyle == option) Color.White else Color.Black
                        )
                    }
                }
            }

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
                                Color.LightGray
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
                        captionStyle.copy(border = CaptionStyle.Border(Color.Black, 2.dp))
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

            Divider()

            // Text Stroke
            Text(
                "Inner Stroke",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.subtitle2
            )
            ColorPicker(
                selectedColor = captionStyle.textStrokeColor,
                onColorSelected = { captionStyle = captionStyle.copy(textStrokeColor = it) },
                modifier = Modifier.padding(16.dp)
            )
            NumericSlider(
                label = "Width",
                value = captionStyle.textStrokeWidth.value,
                onValueChange = { captionStyle = captionStyle.copy(textStrokeWidth = it.dp) },
                valueRange = 0f..20f,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Text(
                "Outer Stroke",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.subtitle2
            )
            ColorPicker(
                selectedColor = captionStyle.textStrokeColorExt,
                onColorSelected = { captionStyle = captionStyle.copy(textStrokeColorExt = it) },
                modifier = Modifier.padding(16.dp)
            )
            NumericSlider(
                label = "Width",
                value = captionStyle.textStrokeWidthExt.value,
                onValueChange = { captionStyle = captionStyle.copy(textStrokeWidthExt = it.dp) },
                valueRange = 0f..20f,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}
