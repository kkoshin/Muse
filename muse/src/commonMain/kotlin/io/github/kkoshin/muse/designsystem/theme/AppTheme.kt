package io.github.kkoshin.muse.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import io.github.kkoshin.muse.platformbridge.LocalToaster
import io.github.kkoshin.muse.platformbridge.ToastManager
import org.koin.compose.koinInject
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.Colors
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.TextStyles
import top.yukonga.miuix.kmp.theme.ThemeColorSpec
import top.yukonga.miuix.kmp.theme.ThemeController

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val toastManagerImpl = koinInject<ToastManager>()

    val controller = remember {
        ThemeController(
            ColorSchemeMode.System, keyColor = Color(
                0xFF5D9CED,
            ),
            colorSpec = ThemeColorSpec.Spec2025
        )
    }

    CompositionLocalProvider(
        LocalToaster provides toastManagerImpl
    ) {
        MiuixTheme(
            controller = controller,
            content = content
        )
    }
}

object AppTheme {
    val colorScheme: Colors
        @Composable @ReadOnlyComposable
        get() = MiuixTheme.colorScheme

    val textStyles: TextStyles
        @Composable @ReadOnlyComposable
        get() = MiuixTheme.textStyles
}