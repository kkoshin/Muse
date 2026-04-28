package io.github.kkoshin.fancy.data

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import io.github.kkoshin.fancy.config.FancyConfig
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HitTestTest {
    private val config = FancyConfig(
        referenceWidth = 1000f,
        referenceHeight = 1000f,
        referenceFontSize = 20f
    )

    @Test
    fun testHitDetection() {
        val containerSize = Size(1000f, 1000f)
        val density = 1f
        val textWidth = 100f
        val textHeight = 50f
        
        val transform = CaptionTransform(
            offset = RelativeOffset(0.5f, 0.5f), // Center
            scale = 1.0f
        )
        val style = CaptionStyle()
        val caption = Caption("Test", style)

        // Center is (500, 500)
        // Bounds should be (450, 475, 550, 525) roughly
        
        assertTrue(
            CaptionHitTester.isHit(
                point = Offset(500f, 500f),
                caption = caption,
                transform = transform,
                config = config,
                containerSize = containerSize,
                density = density,
                textWidth = textWidth,
                textHeight = textHeight
            ),
            "Should hit center"
        )

        assertFalse(
            CaptionHitTester.isHit(
                point = Offset(0f, 0f),
                caption = caption,
                transform = transform,
                config = config,
                containerSize = containerSize,
                density = density,
                textWidth = textWidth,
                textHeight = textHeight
            ),
            "Should not hit top-left"
        )
    }

    @Test
    fun testHitDetectionWithScale() {
        val containerSize = Size(1000f, 1000f)
        val density = 1f
        val textWidth = 100f
        val textHeight = 50f
        
        val transform = CaptionTransform(
            offset = RelativeOffset(0.5f, 0.5f),
            scale = 2.0f
        )
        val caption = Caption("Test", CaptionStyle())

        // Center is (500, 500)
        // Text size scaled: 200x100
        // Bounds: (400, 450, 600, 550)
        
        assertTrue(
            CaptionHitTester.isHit(
                point = Offset(410f, 460f),
                caption = caption,
                transform = transform,
                config = config,
                containerSize = containerSize,
                density = density,
                textWidth = textWidth,
                textHeight = textHeight
            ),
            "Should hit within scaled bounds"
        )

        assertFalse(
            CaptionHitTester.isHit(
                point = Offset(390f, 450f),
                caption = caption,
                transform = transform,
                config = config,
                containerSize = containerSize,
                density = density,
                textWidth = textWidth,
                textHeight = textHeight
            ),
            "Should not hit just outside scaled bounds"
        )

        assertTrue(
            CaptionHitTester.isHit(
                point = Offset(390f, 450f),
                caption = caption,
                transform = transform,
                config = config,
                containerSize = containerSize,
                density = density,
                textWidth = textWidth,
                textHeight = textHeight,
                paddingThreshold = 20.dp
            ),
            "Should hit with padding threshold"
        )
    }
}
