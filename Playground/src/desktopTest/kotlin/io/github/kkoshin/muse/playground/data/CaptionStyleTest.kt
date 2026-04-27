package io.github.kkoshin.muse.playground.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import io.github.kkoshin.muse.playground.Constants
import kotlin.test.Test
import kotlin.test.assertEquals

class CaptionStyleTest {

    @Test
    fun testToTextStyle() {
        val style = CaptionStyle(textColor = Color.Red)
        val density = 2.0f
        val textStyle = style.toTextStyle(density)
        
        assertEquals(Color.Red, textStyle.color)
        val expectedSize = (Constants.REFERENCE_FONT_SIZE * 1.0f * density).sp
        assertEquals(expectedSize, textStyle.fontSize)
        assertEquals(TextAlign.Center, textStyle.textAlign)
    }

    @Test
    fun testToTextStyleWithFontScale() {
        val style = CaptionStyle(textColor = Color.Red, fontScale = 1.5f)
        val density = 2.0f
        val textStyle = style.toTextStyle(density)
        
        assertEquals(Color.Red, textStyle.color)
        val expectedSize = (Constants.REFERENCE_FONT_SIZE * 1.5f * density).sp
        assertEquals(expectedSize, textStyle.fontSize)
        assertEquals(TextAlign.Center, textStyle.textAlign)
    }
}
