package io.github.kkoshin.muse.playground.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import io.github.kkoshin.muse.playground.data.CaptionTransform

@Composable
fun DebugInfo(
    modifier: Modifier,
    captionTransform: CaptionTransform
) {
    val density = LocalDensity.current.density
    Column(modifier.padding(16.dp)) {
        Text("""
            scale: ${captionTransform.scale}
            offsetX: ${captionTransform.offset.x}
            offsetY: ${captionTransform.offset.y}
            density: $density
        """.trimIndent())
    }
}
