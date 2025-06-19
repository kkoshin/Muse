package io.github.kkoshin.muse.feature.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import io.github.kkoshin.muse.platformbridge.LocalToaster
import io.github.kkoshin.muse.platformbridge.ToastManager
import org.koin.compose.koinInject

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val toastManagerImpl = koinInject<ToastManager>()

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
        CompositionLocalProvider(
            LocalToaster provides toastManagerImpl
        ) {
            content()
        }
    }
}