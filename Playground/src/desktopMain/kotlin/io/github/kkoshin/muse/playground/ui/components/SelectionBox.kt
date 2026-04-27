package io.github.kkoshin.muse.playground.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Expand
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

// action menu: zoom, delete
@Composable
fun SelectionBox(
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {},
    onScaleDrag: (androidx.compose.ui.geometry.Offset) -> Unit = {}
) {
    Box(
        modifier
    ) {
        Spacer(
            Modifier
                .matchParentSize()
                .border(2.dp, MaterialTheme.colors.secondary, RoundedCornerShape(16.dp))
        )
        IconButton(
            modifier = Modifier.align(Alignment.TopEnd).offset(24.dp, y = -24.dp),
            onClick = onClose) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colors.onSecondary,
                modifier = Modifier.background(MaterialTheme.colors.primary, CircleShape)
                    .padding(8.dp)
            )
        }

        IconButton(
            modifier = Modifier.align(Alignment.BottomEnd).offset(24.dp, y = 24.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        onScaleDrag(dragAmount)
                    }
                },
            onClick = {}) {
            Icon(
                Icons.Default.Expand, contentDescription = "Scale",
                tint = MaterialTheme.colors.onSecondary,
                modifier = Modifier.background(MaterialTheme.colors.primary, CircleShape)
                    .padding(8.dp)
            )
        }
    }
}