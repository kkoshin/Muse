package io.github.kkoshin.fancy.config

import kotlin.test.Test
import kotlin.test.assertEquals

class FancyConfigTest {
    @Test
    fun testFancyConfig() {
        val config = FancyConfig(
            referenceWidth = 1920f,
            referenceHeight = 1080f,
            referenceFontSize = 40f
        )
        assertEquals(1920f, config.referenceWidth)
        assertEquals(1080f, config.referenceHeight)
        assertEquals(40f, config.referenceFontSize)
    }
}
