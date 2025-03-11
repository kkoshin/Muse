package io.github.kkoshin.muse.app

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color

private val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
private val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)

@Composable
fun AppTheme(activity: ComponentActivity, content: @Composable () -> Unit) {
    val darkTheme = isSystemInDarkTheme()

    DisposableEffect(darkTheme) {
        activity.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            ) { darkTheme },
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim,
                darkScrim,
            ) { darkTheme },
        )
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