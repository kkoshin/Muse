package io.github.kkoshin.muse.playground.core

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.kkoshin.muse.playground.data.Caption
import io.github.kkoshin.muse.playground.data.CaptionTransform
import io.github.kkoshin.muse.playground.ui.drawCaption
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import java.io.File

class ExportManager {

    fun exportToFile(
        caption: Caption,
        captionTransform: CaptionTransform,
        width: Int,
        height: Int,
        textMeasurer: TextMeasurer,
        file: File
    ) {
        val bitmap = exportToBitmap(caption, captionTransform, width, height, textMeasurer)
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
        height: Int,
        textMeasurer: TextMeasurer
    ): Bitmap {
        val bitmap = ImageBitmap(width, height)
        val canvas = Canvas(bitmap)

        val drawScope = CanvasDrawScope()
        val size = Size(width.toFloat(), height.toFloat())

        drawScope.draw(
            density = Density(1f), // 根据导出分辨率设定密度
            layoutDirection = LayoutDirection.Ltr,
            canvas = canvas,
            size = size
        ) {
            drawCaption(captionTransform, caption, textMeasurer)
        }
        return bitmap.asSkiaBitmap()
    }
}