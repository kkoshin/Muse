package io.github.kkoshin.muse.playground.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
fun ColorPicker(
    selectedColor: ComposeColor,
    onColorSelected: (ComposeColor) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        ComposeColor.Black, ComposeColor.White, ComposeColor.Red, ComposeColor.Green, ComposeColor.Blue,
        ComposeColor.Yellow, ComposeColor.Cyan, ComposeColor.Magenta, ComposeColor.Gray, ComposeColor.DarkGray
    )

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            colors.take(5).forEach { color ->
                ColorItem(
                    color = color,
                    isSelected = color == selectedColor,
                    onClick = { onColorSelected(color) }
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            colors.drop(5).forEach { color ->
                ColorItem(
                    color = color,
                    isSelected = color == selectedColor,
                    onClick = { onColorSelected(color) }
                )
            }
        }
    }
}

@Composable
private fun ColorItem(
    color: ComposeColor,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colors.primary else ComposeColor.LightGray,
                shape = CircleShape
            )
            .clickable { onClick() }
    )
}
