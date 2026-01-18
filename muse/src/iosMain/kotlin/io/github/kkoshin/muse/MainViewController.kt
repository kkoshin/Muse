package io.github.kkoshin.muse

import androidx.compose.ui.window.ComposeUIViewController
import io.github.kkoshin.muse.feature.theme.AppTheme
import org.koin.compose.KoinApplication

fun MainViewController() = ComposeUIViewController {
    KoinApplication(application = {
        modules(appModule)
    }) {
        AppTheme {
            MainScreen()
        }
    }
}

