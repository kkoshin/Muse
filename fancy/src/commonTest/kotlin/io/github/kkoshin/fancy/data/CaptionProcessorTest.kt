package io.github.kkoshin.fancy.data

import kotlin.test.Test
import kotlin.test.assertEquals

class CaptionProcessorTest {

    @Test
    fun testHighlightMiddleOdd() {
        val text = "abcde" // length 5, middle index 2 ('c')
        val style = CaptionStyle()
        val segments = CaptionProcessor.processHighlight(text, style)

        assertEquals(3, segments.size)
        assertEquals("ab", segments[0].text)
        assertEquals(null, segments[0].styleOverride)

        assertEquals("c", segments[1].text)
        assertEquals(style.highlightStyle, segments[1].styleOverride)

        assertEquals("de", segments[2].text)
        assertEquals(null, segments[2].styleOverride)
    }

    @Test
    fun testHighlightMiddleEven() {
        val text = "abcd" // length 4, middle indices 1, 2 ('bc')
        val style = CaptionStyle()
        val segments = CaptionProcessor.processHighlight(text, style)

        assertEquals(3, segments.size)
        assertEquals("a", segments[0].text)
        assertEquals(null, segments[0].styleOverride)

        assertEquals("bc", segments[1].text)
        assertEquals(style.highlightStyle, segments[1].styleOverride)

        assertEquals("d", segments[2].text)
        assertEquals(null, segments[2].styleOverride)
    }

    @Test
    fun testHighlightMultiLine() {
        val text = "abc\ndefg" 
        val style = CaptionStyle()
        val segments = CaptionProcessor.processHighlight(text, style)

        assertEquals(6, segments.size)
        assertEquals("a", segments[0].text)
        assertEquals("b", segments[1].text)
        assertEquals("c\n", segments[2].text)
        assertEquals("d", segments[3].text)
        assertEquals("ef", segments[4].text)
        assertEquals("g", segments[5].text)
    }

    @Test
    fun testHighlightShortStrings() {
        val style = CaptionStyle()
        
        // Single char
        val segments1 = CaptionProcessor.processHighlight("a", style)
        assertEquals(1, segments1.size)
        assertEquals("a", segments1[0].text)
        assertEquals(style.highlightStyle, segments1[0].styleOverride)

        // Empty
        val segments2 = CaptionProcessor.processHighlight("", style)
        assertEquals(0, segments2.size)
    }
}
