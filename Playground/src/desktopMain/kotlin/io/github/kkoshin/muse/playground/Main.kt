package io.github.kkoshin.muse.playground

import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.kkoshin.muse.appModule
import io.github.kkoshin.muse.feature.theme.AppTheme
import io.github.kkoshin.muse.playground.ui.CaptionView
import org.koin.core.context.startKoin
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun main() = application {
    startKoin {
        modules(appModule)
    }

    val density = LocalDensity.current
    Window(
        onCloseRequest = ::exitApplication,
        title = "Muse Playground",
        state = rememberWindowState(
            width = (Constants.REFERENCE_WIDTH / density.density.toInt() + 300).dp,
            height = (Constants.REFERENCE_HEIGHT / density.density.toInt()).dp
        )
    ) {
        AppTheme {
            CaptionView()
        }
    }
}
