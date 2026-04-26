package io.github.kkoshin.muse.playground.ui

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import io.github.kkoshin.muse.playground.data.Caption
import io.github.kkoshin.muse.playground.data.CaptionTransform

// 这是一个纯绘制逻辑，不依赖 Composable 上下文，只依赖 DrawScope
fun DrawScope.drawCaption(
    captionTransform: CaptionTransform,
    caption: Caption,
    textMeasurer: TextMeasurer,
) {
    val style = caption.style
    val textResult = textMeasurer.measure(
        text = caption.text,
        style = TextStyle(color = Color(style.textColor.rgb))
    )

    val offsetX = captionTransform.offset.x.toPx()
    val offsetY = captionTransform.offset.y.toPx()

    // 1. 应用变换 (对应数据模型中的位置、缩放)
    withTransform({
        translate(offsetX, offsetY)
        scale(captionTransform.scale, pivot = Offset.Zero)
    }) {
        // Draw background
        style.background?.let { bg ->
            val padding = bg.contentPadding.toPx()
            val radius = bg.radius.toPx()
            drawRoundRect(
                color = Color(bg.color.rgb),
                topLeft = Offset(-padding, -padding),
                size = Size(
                    textResult.size.width.toFloat() + padding * 2,
                    textResult.size.height.toFloat() + padding * 2
                ),
                cornerRadius = CornerRadius(radius, radius)
            )
        }

        // Draw border
        style.border?.let { b ->
            val padding = style.background?.contentPadding?.toPx() ?: 0f
            val width = b.width.toPx()
            val radius = style.background?.radius?.toPx() ?: 0f
            drawRoundRect(
                color = Color(b.color.rgb),
                topLeft = Offset(-padding - width / 2, -padding - width / 2),
                size = Size(
                    textResult.size.width.toFloat() + (padding + width / 2) * 2,
                    textResult.size.height.toFloat() + (padding + width / 2) * 2
                ),
                style = Stroke(width = width),
                cornerRadius = CornerRadius(radius + width / 2, radius + width / 2)
            )
        }

        drawText(textResult)
    }
}
