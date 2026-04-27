package io.github.kkoshin.muse.playground.data

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.test.Test
import kotlin.test.assertEquals

class CaptionTransformTest {

    @Test
    fun testToOffset() {
        val containerSize = Size(1000f, 500f)
        val relativeOffset = RelativeOffset(0.5f, 0.2f)
        
        val offset = relativeOffset.toOffset(containerSize)
        
        assertEquals(500f, offset.x)
        assertEquals(100f, offset.y)
    }

    @Test
    fun testToRelative() {
        val containerSize = Size(1000f, 500f)
        val offset = Offset(250f, 250f)
        
        val relative = offset.toRelative(containerSize)
        
        assertEquals(0.25f, relative.x)
        assertEquals(0.5f, relative.y)
    }

    @Test
    fun testToRelativeZeroSize() {
        val containerSize = Size.Zero
        val offset = Offset(100f, 100f)
        
        val relative = offset.toRelative(containerSize)
        
        assertEquals(0f, relative.x)
        assertEquals(0f, relative.y)
    }
}
