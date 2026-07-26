package io.github.kkoshin.muse.playground

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.kkoshin.muse.appModule
import io.github.kkoshin.muse.designsystem.component.MuseButton
import io.github.kkoshin.muse.designsystem.theme.MuseTheme
import io.github.kkoshin.muse.feature.theme.AppTheme
import io.github.kkoshin.muse.playground.ui.CaptionView
import io.github.kkoshin.muse.playground.ui.DesignSystemPreview
import org.koin.core.context.startKoin
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun main() = application {
    startKoin {
        modules(appModule)
    }

    val density = LocalDensity.current
    var showDesignSystem by remember { mutableStateOf(true) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Muse Playground",
        state = rememberWindowState(
            width = (Constants.REFERENCE_WIDTH / density.density.toInt() + 300).dp,
            height = (Constants.REFERENCE_HEIGHT / density.density.toInt()).dp
        )
    ) {
        if (showDesignSystem) {
            MuseTheme {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        MuseButton(onClick = { showDesignSystem = false }) {
                            Text("Switch to Caption View")
                        }
                    }
                    DesignSystemPreview()
                }
            }
        } else {
            AppTheme {
                CompositionLocalProvider(
                    LocalDensity provides Density(density.density, fontScale = 1.0f)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            MuseTheme {
                                MuseButton(onClick = { showDesignSystem = true }) {
                                    Text("Switch to DS Preview")
                                }
                            }
                        }
                        CaptionView()
                    }
                }
            }
        }
    }
}
