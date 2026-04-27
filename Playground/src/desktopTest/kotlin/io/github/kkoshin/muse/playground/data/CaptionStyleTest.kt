package io.github.kkoshin.muse.playground.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import io.github.kkoshin.muse.playground.Constants
import kotlin.test.Test
import kotlin.test.assertEquals

class CaptionStyleTest {

    @Test
    fun testToTextStyle() {
        val style = CaptionStyle(textColor = Color.Red)
        val density = 2.0f
        val textMeasurerDensity = Density(density, 1f)
        val textStyle = style.toTextStyle()
        
        assertEquals(Color.Red, textStyle.color)
        val expectedSize = (Constants.REFERENCE_FONT_SIZE * 1.0f).sp
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
        val textStyle = style.toTextStyle()
        assertEquals(0.5.em, textStyle.letterSpacing)
    }

    @Test
    fun testToTextStyleWithBold() {
        val style = CaptionStyle(textStyle = CaptionStyle.TextStyleOption.Bold)
        val textStyle = style.toTextStyle()
        assertEquals(FontWeight.Bold, textStyle.fontWeight)
    }

    @Test
    fun testToTextStyleWithItalic() {
        val style = CaptionStyle(textStyle = CaptionStyle.TextStyleOption.Italic)
        val textStyle = style.toTextStyle()
        assertEquals(FontStyle.Italic, textStyle.fontStyle)
    }

    @Test
    fun testToTextStyleWithUnderline() {
        val style = CaptionStyle(textStyle = CaptionStyle.TextStyleOption.Underline)
        val textStyle = style.toTextStyle()
        assertEquals(TextDecoration.Underline, textStyle.textDecoration)
    }

    @Test
    fun testToTextStyleWithFontScale() {
        val style = CaptionStyle(textColor = Color.Red, fontScale = 1.5f)
        val textStyle = style.toTextStyle()
        
        assertEquals(Color.Red, textStyle.color)
        val expectedSize = (Constants.REFERENCE_FONT_SIZE * 1.5f).sp
        assertEquals(expectedSize, textStyle.fontSize)
        assertEquals(TextAlign.Center, textStyle.textAlign)
    }

    @Test
    fun testHighlightStyleToTextStyle() {
        val highlightStyle = CaptionStyle.HighlightStyle(textColor = Color.Blue, fontScale = 3.0f)
        val textStyle = highlightStyle.toTextStyle()

        assertEquals(Color.Blue, textStyle.color)
        val expectedSize = (Constants.REFERENCE_FONT_SIZE * 3.0f).sp
        assertEquals(expectedSize, textStyle.fontSize)
    }

    @Test
    fun testDefaultHighlightStyle() {
        val style = CaptionStyle()
        assertEquals(Color.Red, style.highlightStyle.textColor)
        assertEquals(2.0f, style.highlightStyle.fontScale)
    }
}
