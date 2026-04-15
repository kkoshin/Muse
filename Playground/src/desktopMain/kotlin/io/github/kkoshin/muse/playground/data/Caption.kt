package io.github.kkoshin.muse.playground.data

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import java.awt.Color

data class Caption(
    val text: String,
    val style: CaptionStyle
)

data class CaptionStyle(
    val textColor: Color = Color.BLACK,
    val border: Border? = null,
    val background: Background? = null
) {
    data class Border(
        val color: Color,
        val width: Dp,
    )

    data class Background(
        val contentPadding: Dp,
        val radius: Dp,
        val color: Color,
    )
}