package io.github.kkoshin.muse.playground.core

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.createFontFamilyResolver
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.kkoshin.fancy.config.FancyConfig
import io.github.kkoshin.fancy.data.Caption
import io.github.kkoshin.fancy.data.CaptionTransform
import io.github.kkoshin.fancy.ui.drawCaption
import io.github.kkoshin.muse.playground.Constants
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import java.io.File

class ExportManager {

    private val config = FancyConfig(
        referenceWidth = Constants.REFERENCE_WIDTH,
        referenceHeight = Constants.REFERENCE_HEIGHT,
        referenceFontSize = Constants.REFERENCE_FONT_SIZE.toFloat()
    )

    fun exportToFile(
        caption: Caption,
        captionTransform: CaptionTransform,
        width: Int,
        height: Int,
        file: File
    ) {
        val bitmap = exportToBitmap(caption, captionTransform, width, height)
        val image = Image.makeFromBitmap(bitmap)
        val data = image.encodeToData(EncodedImageFormat.PNG)
        if (data != null) {
            file.writeBytes(data.bytes)
        }
    }

    fun exportToBitmap(
        caption: Caption,
        captionTransform: CaptionTransform,
        width: Int,
        height: Int
    ): org.jetbrains.skia.Bitmap {
        val bitmap = ImageBitmap(width, height)
        val canvas = Canvas(bitmap)

        val drawScope = CanvasDrawScope()
        val size = Size(width.toFloat(), height.toFloat())
        
        // 以参考宽度为基准计算导出密度
        // 这样在 drawCaption 里的 previewScale 始终为 1.0，
        // 而所有的 dp 值（padding, border）会根据分辨率自动缩放。
        val exportDensity = width / Constants.REFERENCE_WIDTH
        val density = Density(exportDensity)

        val textMeasurer = TextMeasurer(
            defaultFontFamilyResolver = createFontFamilyResolver(),
            defaultDensity = density,
            defaultLayoutDirection = LayoutDirection.Ltr
        )

        drawScope.draw(
            density = density, 
            layoutDirection = LayoutDirection.Ltr,
            canvas = canvas,
            size = size
        ) {
            drawCaption(captionTransform, caption, textMeasurer, config)
        }
        return bitmap.asSkiaBitmap()
    }
}
