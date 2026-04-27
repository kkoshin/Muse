package io.github.kkoshin.fancy.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.rememberTextMeasurer
import io.github.kkoshin.fancy.config.FancyConfig
import io.github.kkoshin.fancy.data.Caption
import io.github.kkoshin.fancy.data.CaptionTransform

@Composable
fun FancyCaptionView(
    caption: Caption,
    captionTransform: CaptionTransform,
    config: FancyConfig,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier) {
        drawCaption(
            captionTransform = captionTransform,
            caption = caption,
            textMeasurer = textMeasurer,
            config = config
        )
    }
}
