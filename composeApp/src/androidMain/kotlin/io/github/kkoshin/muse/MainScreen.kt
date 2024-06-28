package io.github.kkoshin.muse

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import io.github.kkoshin.muse.editor.EditorArgs
import io.github.kkoshin.muse.editor.EditorScreen
import io.github.kkoshin.muse.script.ScriptArgs
import io.github.kkoshin.muse.script.ScriptScreen
import io.github.kkoshin.muse.setting.SettingArgs
import io.github.kkoshin.muse.setting.SettingScreen
import io.github.kkoshin.muse.tts.voice.VoicePicker
import io.github.kkoshin.muse.tts.voice.VoicePickerArgs

@Composable
fun MainScreen(navController: NavHostController = rememberNavController()) {
    MaterialTheme(colors = lightColors(primary = Color(0xFF5D9CED))) {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = ScriptArgs,
        ) {
            composable<ScriptArgs>(
                deepLinks = listOf(
                    navDeepLink {
                        mimeType = "text/plain"
                        action = Intent.ACTION_VIEW
                    },
                ),
            ) { entry ->
                val deepLinkContentUri: Uri? =
                    (entry.arguments?.get(NavController.KEY_DEEP_LINK_INTENT) as? Intent)?.data
                ScriptScreen(
                    contentUri = deepLinkContentUri?.toString(),
                    onRequest = { phrases ->
                        navController.navigate(EditorArgs(phrases))
                    },
                    onLaunchSettingsPage = {
                        navController.navigate(SettingArgs) {
                            launchSingleTop = true
                        }
                    },
                )
            }

            composable<SettingArgs> {
                SettingScreen()
            }

            composable<EditorArgs> { entry ->
                EditorScreen(args = entry.toRoute(), onLaunchVoicePicker = {
                    navController.navigate(VoicePickerArgs(selectedVoiceId = it))
                }) { pcm, audio ->
                    // do nothing.
                }
            }

            composable<VoicePickerArgs> { entry ->
                val args = entry.toRoute<VoicePickerArgs>()
                VoicePicker(
                    selectedVoiceId = args.selectedVoiceId,
                ) {
                    navController.popBackStack()
                }
            }
        }
    }
}