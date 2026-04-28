package io.github.kkoshin.fancy.data

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import io.github.kkoshin.fancy.config.FancyConfig

object CaptionHitTester {
    fun calculateBounds(
        caption: Caption,
        transform: CaptionTransform,
        config: FancyConfig,
        containerSize: Size,
        density: Float,
        textWidth: Float,
        textHeight: Float
    ): Rect {
        val style = caption.style
        
        val padding = style.background?.contentPadding?.toPx(density) ?: 0f
        val borderPadding = (style.border?.width?.toPx(density) ?: 0f) / 2
        val totalPadding = padding + borderPadding

        val contentWidth = textWidth + totalPadding * 2
        val contentHeight = textHeight + totalPadding * 2

        val previewScale = (containerSize.width / density) / config.referenceWidth
        val totalScale = previewScale * transform.scale

        val centerPixelOffset = transform.offset.toOffset(containerSize)
        
        val scaledWidth = contentWidth * totalScale
        val scaledHeight = contentHeight * totalScale
        
        return Rect(
            left = centerPixelOffset.x - scaledWidth / 2,
            top = centerPixelOffset.y - scaledHeight / 2,
            right = centerPixelOffset.x + scaledWidth / 2,
            bottom = centerPixelOffset.y + scaledHeight / 2
        )
    }

    fun isHit(
        point: Offset,
        caption: Caption,
        transform: CaptionTransform,
        config: FancyConfig,
        containerSize: Size,
        density: Float,
        textWidth: Float,
        textHeight: Float,
        paddingThreshold: Dp = Dp.Unspecified
    ): Boolean {
        val bounds = calculateBounds(
            caption = caption,
            transform = transform,
            config = config,
            containerSize = containerSize,
            density = density,
            textWidth = textWidth,
            textHeight = textHeight
        )
        
        val effectiveBounds = if (paddingThreshold != Dp.Unspecified) {
            val p = paddingThreshold.toPx(density)
            Rect(
                left = bounds.left - p,
                top = bounds.top - p,
                right = bounds.right + p,
                bottom = bounds.bottom + p
            )
        } else {
            bounds
        }
        
        return effectiveBounds.contains(point)
    }

    private fun Dp.toPx(density: Float): Float = value * density
}
