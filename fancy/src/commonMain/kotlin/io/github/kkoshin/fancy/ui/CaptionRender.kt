package io.github.kkoshin.fancy.ui

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        caption.segments.toAnnotatedString(config, style)
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

        // Helper to draw a stroke layer
        fun drawStrokeLayer(isOuter: Boolean) {
            val strokeAnnotatedString = if (caption.segments.isNotEmpty()) {
                androidx.compose.ui.text.buildAnnotatedString {
                    caption.segments.forEach { seg ->
                        val segWidth = if (isOuter) seg.styleOverride?.textStrokeWidthExt else seg.styleOverride?.textStrokeWidth
                        val segColor = if (isOuter) seg.styleOverride?.textStrokeColorExt else seg.styleOverride?.textStrokeColor
                        val segFontScale = seg.styleOverride?.fontScale ?: style.fontScale

                        val effectiveWidth = segWidth ?: (if (isOuter) style.textStrokeWidthExt else style.textStrokeWidth)
                        val effectiveColor = segColor ?: (if (isOuter) style.textStrokeColorExt else style.textStrokeColor)

                        val scaledWidth = effectiveWidth.toPx() * segFontScale
                        
                        pushStyle(
                            androidx.compose.ui.text.SpanStyle(
                                color = if (scaledWidth > 0f) effectiveColor else Color.Transparent,
                                fontSize = (config.referenceFontSize * segFontScale).sp,
                                drawStyle = if (scaledWidth > 0f) Stroke(width = scaledWidth, join = StrokeJoin.Round) else null
                            )
                        )
                        append(seg.text)
                        pop()
                    }
                }
            } else {
                val color = if (isOuter) style.textStrokeColorExt else style.textStrokeColor
                val width = if (isOuter) style.textStrokeWidthExt else style.textStrokeWidth
                val scaledWidth = width.toPx() * style.fontScale
                androidx.compose.ui.text.buildAnnotatedString {
                    pushStyle(
                        androidx.compose.ui.text.SpanStyle(
                            color = if (scaledWidth > 0f) color else Color.Transparent,
                            fontSize = (config.referenceFontSize * style.fontScale).sp,
                            drawStyle = if (scaledWidth > 0f) Stroke(width = scaledWidth, join = StrokeJoin.Round) else null
                        )
                    )
                    append(caption.text)
                    pop()
                }
            }

            val strokeResult = textMeasurer.measure(
                text = strokeAnnotatedString,
                style = style.toTextStyle(config)
            )

            drawText(
                textLayoutResult = strokeResult,
                topLeft = Offset(totalPadding, totalPadding)
            )
        }

        // 1. Draw Outer Stroke
        drawStrokeLayer(isOuter = true)

        // 2. Draw Inner Stroke
        drawStrokeLayer(isOuter = false)

        // 3. Draw Fill
        drawText(textResult, topLeft = Offset(totalPadding, totalPadding))
    }
}
