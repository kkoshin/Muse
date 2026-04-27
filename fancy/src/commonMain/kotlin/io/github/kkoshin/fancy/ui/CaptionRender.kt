package io.github.kkoshin.fancy.ui

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import io.github.kkoshin.fancy.config.FancyConfig
import io.github.kkoshin.fancy.data.Caption
import io.github.kkoshin.fancy.data.CaptionTransform
import io.github.kkoshin.fancy.data.toAnnotatedString
import io.github.kkoshin.fancy.data.toOffset
import io.github.kkoshin.fancy.data.toTextStyle

/**
 * Core drawing logic for captions.
 * This is a pure drawing function that does not depend on Composable context.
 */
fun DrawScope.drawCaption(
    captionTransform: CaptionTransform,
    caption: Caption,
    textMeasurer: TextMeasurer,
    config: FancyConfig
) {
    val style = caption.style
    val currentDensity = density

    val textLayoutInput = if (caption.segments.isNotEmpty()) {
        caption.segments.toAnnotatedString(config)
    } else {
        caption.text
    }

    val textResult = if (textLayoutInput is String) {
        textMeasurer.measure(
            text = textLayoutInput,
            style = caption.style.toTextStyle(config),
        )
    } else {
        textMeasurer.measure(
            text = textLayoutInput as androidx.compose.ui.text.AnnotatedString,
            style = caption.style.toTextStyle(config),
        )
    }

    // Convert from RelativeOffset to pixel coordinates on the current Canvas (as center point)
    val centerPixelOffset = captionTransform.offset.toOffset(size)

    val padding = style.background?.contentPadding?.toPx() ?: 0f
    val borderPadding = (style.border?.width?.toPx() ?: 0f) / 2
    val totalPadding = padding + borderPadding

    val contentWidth = textResult.size.width.toFloat() + totalPadding * 2
    val contentHeight = textResult.size.height.toFloat() + totalPadding * 2

    // 1. Apply transformations (position, scale)
    withTransform({
        // Translate center point to target position
        translate(centerPixelOffset.x, centerPixelOffset.y)

        // Apply preview scale (based on reference width)
        val previewScale = (size.width / currentDensity) / config.referenceWidth
        scale(previewScale, previewScale, pivot = Offset.Zero)

        scale(captionTransform.scale, captionTransform.scale, pivot = Offset.Zero)
        // Translate to top-left corner by half the size, so RelativeOffset becomes the center
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
