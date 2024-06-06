package com.github.kkoshin.muse

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.github.kkoshin.muse.editor.EditorArgs
import com.github.kkoshin.muse.editor.EditorScreen
import com.github.kkoshin.muse.export.ExportArgs
import com.github.kkoshin.muse.export.ExportScreen
import com.github.kkoshin.muse.script.ScriptArgs
import com.github.kkoshin.muse.script.ScriptScreen
import kotlinx.serialization.Serializable

@Serializable
object HomeArgs

@Composable
fun MainScreen() {
    MaterialTheme(colors = lightColors(primary = Color(0xFF5D9CED))) {
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
                EditorScreen(args = it.toRoute()) { uri ->
                    navController.navigate(ExportArgs(uri.toString()))
                }
            }

            composable<ExportArgs> {
                ExportScreen(args = it.toRoute())
            }
        }
    }
}