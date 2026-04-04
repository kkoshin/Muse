package io.github.kkoshin.muse.playground

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.kkoshin.muse.appModule
import io.github.kkoshin.muse.feature.dashboard.ScriptCreatorScreen
import io.github.kkoshin.muse.feature.theme.AppTheme
import org.koin.core.context.startKoin
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun main() = application {
    startKoin {
        modules(appModule)
    }

    Window(onCloseRequest = ::exitApplication, title = "Muse Playground") {
        AppTheme {
            ScriptCreatorScreen(
                onResult = { scriptId ->
                    println("Result: $scriptId")
                }
            )
        }
    }
}
