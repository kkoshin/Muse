package io.github.kkoshin.muse.playground.ui

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import io.github.kkoshin.muse.playground.Constants
import io.github.kkoshin.muse.playground.data.Caption
import io.github.kkoshin.muse.playground.data.CaptionTransform
import io.github.kkoshin.muse.playground.data.toAnnotatedString
import io.github.kkoshin.muse.playground.data.toOffset
import io.github.kkoshin.muse.playground.data.toTextStyle

// 这是一个纯绘制逻辑，不依赖 Composable 上下文，只依赖 DrawScope
fun DrawScope.drawCaption(
    captionTransform: CaptionTransform,
    caption: Caption,
    textMeasurer: TextMeasurer,
) {
    val style = caption.style
    val currentDensity = density

    val textLayoutInput = if (caption.segments.isNotEmpty()) {
        caption.segments.toAnnotatedString()
    } else {
        caption.text
    }

    val textResult = if (textLayoutInput is String) {
        textMeasurer.measure(
            text = textLayoutInput,
            style = caption.style.toTextStyle(),
        )
    } else {
        textMeasurer.measure(
            text = textLayoutInput as androidx.compose.ui.text.AnnotatedString,
            style = caption.style.toTextStyle(),
        )
    }

    // 从 RelativeOffset 转换为当前 Canvas 上的像素坐标 (作为中心点)
    val centerPixelOffset = captionTransform.offset.toOffset(size)

    val padding = style.background?.contentPadding?.toPx() ?: 0f
    val borderPadding = (style.border?.width?.toPx() ?: 0f) / 2
    val totalPadding = padding + borderPadding

    val contentWidth = textResult.size.width.toFloat() + totalPadding * 2
    val contentHeight = textResult.size.height.toFloat() + totalPadding * 2

    // 1. 应用变换 (对应数据模型中的位置、缩放)
    withTransform({
        // 将中心点平移到目标位置
        translate(centerPixelOffset.x, centerPixelOffset.y)

        // 应用预览缩放 (基于参考宽度)
        val previewScale = (size.width / currentDensity) / Constants.REFERENCE_WIDTH
        scale(previewScale, previewScale, pivot = Offset.Zero)

        scale(captionTransform.scale, captionTransform.scale, pivot = Offset.Zero)
        // 向左上角平移一半尺寸，使 RelativeOffset 成为中心
        translate(-contentWidth / 2, -contentHeight / 2)
    }) {
        // Draw background
        style.background?.let { bg ->
            val bgPadding = bg.contentPadding.toPx()
            val radius = bg.radius.toPx()
            drawRoundRect(
                color = bg.color,
                topLeft = Offset(totalPadding - bgPadding, totalPadding - bgPadding),
                size = Size(
                    textResult.size.width.toFloat() + bgPadding * 2,
                    textResult.size.height.toFloat() + bgPadding * 2
                ),
                cornerRadius = CornerRadius(radius, radius)
            )
        }

        // Draw border
        style.border?.let { b ->
            val bPadding = style.background?.contentPadding?.toPx() ?: 0f
            val width = b.width.toPx()
            val radius = style.background?.radius?.toPx() ?: 0f
            drawRoundRect(
                color = b.color,
                topLeft = Offset(totalPadding - bPadding - width / 2, totalPadding - bPadding - width / 2),
                size = Size(
                    textResult.size.width.toFloat() + (bPadding + width / 2) * 2,
                    textResult.size.height.toFloat() + (bPadding + width / 2) * 2
                ),
                style = Stroke(width = width),
                cornerRadius = CornerRadius(radius + width / 2, radius + width / 2)
            )
        }

        drawText(textResult, topLeft = Offset(totalPadding, totalPadding))
    }
}
