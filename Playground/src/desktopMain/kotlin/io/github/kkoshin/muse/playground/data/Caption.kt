package io.github.kkoshin.muse.playground.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import io.github.kkoshin.muse.playground.Constants.REFERENCE_FONT_SIZE

data class Caption(
    val text: String,
    val style: CaptionStyle,
    val segments: List<CaptionSegment> = emptyList()
)

data class CaptionSegment(
    val text: String,
    val styleOverride: CaptionStyle.HighlightStyle? = null
)

fun List<CaptionSegment>.toAnnotatedString(density: Float): AnnotatedString {
    return buildAnnotatedString {
        forEach { segment ->
            if (segment.styleOverride != null) {
                val spanStyle = SpanStyle(
                    color = segment.styleOverride.textColor,
                    fontSize = (REFERENCE_FONT_SIZE * segment.styleOverride.fontScale * density).sp
                )
                pushStyle(spanStyle)
                append(segment.text)
                pop()
            } else {
                append(segment.text)
            }
        }
    }
}

data class CaptionStyle(
    val textColor: Color = Color.Black,
    val fontScale: Float = 1.0f,
    val border: Border? = null,
    val background: Background? = null,
    val highlightStyle: HighlightStyle = HighlightStyle()
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

    data class HighlightStyle(
        val textColor: Color = Color.Red,
        val fontScale: Float = 2.0f
    )
}

fun CaptionStyle.toTextStyle(density: Float): TextStyle {
    return TextStyle(
        color = textColor,
        fontSize = (REFERENCE_FONT_SIZE * fontScale * density).sp,
        textAlign = TextAlign.Center
    )
}

fun CaptionStyle.HighlightStyle.toTextStyle(density: Float): TextStyle {
    return TextStyle(
        color = textColor,
        fontSize = (REFERENCE_FONT_SIZE * fontScale * density).sp,
    )
}