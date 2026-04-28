package io.github.kkoshin.fancy.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import io.github.kkoshin.fancy.config.FancyConfig
import kotlin.test.Test
import kotlin.test.assertEquals

class CaptionStyleTest {
    private val config = FancyConfig(
        referenceWidth = 1920f,
        referenceHeight = 1080f,
        referenceFontSize = 20f
    )

    @Test
    fun testToTextStyle() {
        val style = CaptionStyle(textColor = Color.Red)
        val textStyle = style.toTextStyle(config)
        
        assertEquals(Color.Red, textStyle.color)
        val expectedSize = (config.referenceFontSize * 1.0f).sp
        assertEquals(expectedSize, textStyle.fontSize)
        assertEquals(TextAlign.Center, textStyle.textAlign)
        assertEquals(0.em, textStyle.letterSpacing)
        assertEquals(FontWeight.Normal, textStyle.fontWeight)
        assertEquals(FontStyle.Normal, textStyle.fontStyle)
        assertEquals(TextDecoration.None, textStyle.textDecoration)
    }

    @Test
    fun testToTextStyleWithLetterSpacing() {
        val style = CaptionStyle(letterSpacing = 0.5f)
        val textStyle = style.toTextStyle(config)
        assertEquals(0.5.em, textStyle.letterSpacing)
    }

    @Test
    fun testToTextStyleWithBold() {
        val style = CaptionStyle(textStyle = CaptionStyle.TextStyleOption.Bold)
        val textStyle = style.toTextStyle(config)
        assertEquals(FontWeight.Bold, textStyle.fontWeight)
    }

    @Test
    fun testToTextStyleWithItalic() {
        val style = CaptionStyle(textStyle = CaptionStyle.TextStyleOption.Italic)
        val textStyle = style.toTextStyle(config)
        assertEquals(FontStyle.Italic, textStyle.fontStyle)
    }

    @Test
    fun testToTextStyleWithUnderline() {
        val style = CaptionStyle(textStyle = CaptionStyle.TextStyleOption.Underline)
        val textStyle = style.toTextStyle(config)
        assertEquals(TextDecoration.Underline, textStyle.textDecoration)
    }

    @Test
    fun testToTextStyleWithFontScale() {
        val style = CaptionStyle(textColor = Color.Red, fontScale = 1.5f)
        val textStyle = style.toTextStyle(config)
        
        assertEquals(Color.Red, textStyle.color)
        val expectedSize = (config.referenceFontSize * 1.5f).sp
        assertEquals(expectedSize, textStyle.fontSize)
        assertEquals(TextAlign.Center, textStyle.textAlign)
    }

    @Test
    fun testHighlightStyleToTextStyle() {
        val highlightStyle = CaptionStyle.HighlightStyle(textColor = Color.Blue, fontScale = 3.0f)
        val textStyle = highlightStyle.toTextStyle(config)

        assertEquals(Color.Blue, textStyle.color)
        val expectedSize = (config.referenceFontSize * 3.0f).sp
        assertEquals(expectedSize, textStyle.fontSize)
    }

    @Test
    fun testDefaultHighlightStyle() {
        val style = CaptionStyle()
        assertEquals(Color.Red, style.highlightStyle.textColor)
        assertEquals(2.0f, style.highlightStyle.fontScale)
    }

    @Test
    fun testCaptionStyleWithStroke() {
        val style = CaptionStyle(
            textStrokeColor = Color.Blue,
            textStrokeWidth = 2.dp,
            textStrokeColorExt = Color.White,
            textStrokeWidthExt = 4.dp
        )
        assertEquals(Color.Blue, style.textStrokeColor)
        assertEquals(2.dp, style.textStrokeWidth)
        assertEquals(Color.White, style.textStrokeColorExt)
        assertEquals(4.dp, style.textStrokeWidthExt)
    }

    @Test
    fun testHighlightStyleWithStrokeDefaults() {
        val style = CaptionStyle()
        val highlight = style.highlightStyle
        assertEquals(Color(0xFF800080), highlight.textStrokeColor)
        assertEquals(2.dp, highlight.textStrokeWidth)
        assertEquals(Color.White, highlight.textStrokeColorExt)
        assertEquals(4.dp, highlight.textStrokeWidthExt)
    }
}
