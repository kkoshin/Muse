package io.github.kkoshin.muse.app

import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color

@Composable
fun AppTheme(activity: ComponentActivity, content: @Composable () -> Unit) {
    val darkTheme = isSystemInDarkTheme()

    DisposableEffect(darkTheme) {
        activity.enableEdgeToEdge()
        onDispose {}
    }

    MaterialTheme(
        colors = if (isSystemInDarkTheme()) {
            darkColors()
        } else {
            lightColors(
                primary = Color(
                    0xFF5D9CED,
                ),
            )
        },
    ) {
        content()
    }
}