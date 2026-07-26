package io.github.kkoshin.muse.feature.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import io.github.kkoshin.muse.designsystem.theme.MuseTheme
import io.github.kkoshin.muse.platformbridge.LocalToaster
import io.github.kkoshin.muse.platformbridge.ToastManager
import org.koin.compose.koinInject

/**
 * App-level theme.
 *
 * Thin wrapper over [MuseTheme] (M3 via designsystem) that provides
 * app-specific [LocalToaster] dependency.
 *
 * Business code does not need to reference [MuseTheme] directly — use this
 * composable at the app root and component wrappers (MuseTopAppBar, etc.)
 * for per-screen needs.
 */
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val toastManagerImpl = koinInject<ToastManager>()

    MuseTheme {
        CompositionLocalProvider(
            LocalToaster provides toastManagerImpl
        ) {
            content()
        }
    }
}