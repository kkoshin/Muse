package com.github.kkoshin.speaker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.github.kkoshin.speaker.editor.EditorArgs
import com.github.kkoshin.speaker.editor.EditorScreen
import com.github.kkoshin.speaker.export.ExportArgs
import com.github.kkoshin.speaker.export.ExportScreen
import com.github.kkoshin.speaker.script.ScriptArgs
import com.github.kkoshin.speaker.script.ScriptScreen
import kotlinx.serialization.Serializable

@Serializable
object HomeArgs

@Composable
fun MainScreen() {
    MaterialTheme {
        val navController = rememberNavController()
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = ScriptArgs,
        ) {
            composable<HomeArgs> {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .systemBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(onClick = {
                        navController.navigate(ScriptArgs)
                    }) {
                        Text("Click me!")
                    }
                }
            }

            composable<ScriptArgs> {
                ScriptScreen { phrases ->
                    navController.navigate(EditorArgs(phrases))
                }
            }

            composable<EditorArgs> {
                EditorScreen(args = it.toRoute()) {
                    navController.navigate(ExportArgs)
                }
            }

            composable<ExportArgs> {
                ExportScreen()
            }
        }
    }
}