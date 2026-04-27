package io.github.kkoshin.fancy.data

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

data class RelativeOffset(
    val x: Float,
    val y: Float
)

data class CaptionTransform(
    val offset: RelativeOffset = RelativeOffset(0.5f, 0.5f),
    val scale: Float = 1.0f
)

fun RelativeOffset.toOffset(containerSize: Size): Offset {
    return Offset(x * containerSize.width, y * containerSize.height)
}

fun Offset.toRelative(containerSize: Size): RelativeOffset {
    return RelativeOffset(
        if (containerSize.width > 0) x / containerSize.width else 0f,
        if (containerSize.height > 0) y / containerSize.height else 0f
    )
}
