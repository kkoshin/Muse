package io.github.kkoshin.muse.playground.ui

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import io.github.kkoshin.muse.playground.data.Caption
import io.github.kkoshin.muse.playground.data.CaptionTransform

// 这是一个纯绘制逻辑，不依赖 Composable 上下文，只依赖 DrawScope
fun DrawScope.drawCaption(
    captionTransform: CaptionTransform,
    caption: Caption,
    textMeasurer: TextMeasurer,
) {
    // 1. 应用变换 (对应数据模型中的位置、缩放)
    withTransform({
        with(this@drawCaption) {
            this@withTransform.translate(captionTransform.offset.x.toPx(), captionTransform.offset.y.toPx())
        }
    }
    ) {
        drawText(textMeasurer, caption.text)
    }
}