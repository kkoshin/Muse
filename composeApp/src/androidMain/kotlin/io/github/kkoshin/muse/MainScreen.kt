package io.github.kkoshin.muse

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.get
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.google.accompanist.navigation.material.bottomSheet
import io.github.kkoshin.muse.dashboard.DashboardArgs
import io.github.kkoshin.muse.dashboard.DashboardScreen
import io.github.kkoshin.muse.dashboard.ScriptCreatorArgs
import io.github.kkoshin.muse.dashboard.ScriptCreatorScreen
import io.github.kkoshin.muse.editor.EditorArgs
import io.github.kkoshin.muse.editor.EditorScreen
import io.github.kkoshin.muse.editor.ExportConfigSheet
import io.github.kkoshin.muse.editor.ExportConfigSheetArgs
import io.github.kkoshin.muse.export.ExportArgs
import io.github.kkoshin.muse.export.ExportScreen
import io.github.kkoshin.muse.export.HistoryArgs
import io.github.kkoshin.muse.export.HistoryScreen
import io.github.kkoshin.muse.navigation.bottomSheet
import io.github.kkoshin.muse.setting.SettingArgs
import io.github.kkoshin.muse.setting.SettingScreen
import io.github.kkoshin.muse.tts.voice.VoicePicker
import io.github.kkoshin.muse.tts.voice.VoicePickerArgs
import java.util.UUID

@Composable
fun MainScreen(navController: NavHostController = rememberNavController()) {
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = DashboardArgs,
    ) {
        composable<DashboardArgs>(
            deepLinks = listOf(
                navDeepLink {
                    mimeType = "text/plain"
                    action = Intent.ACTION_VIEW
                },
            ),
        ) { entry ->
            val deepLinkContentUri: Uri? =
                (entry.arguments?.get(NavController.KEY_DEEP_LINK_INTENT) as? Intent)?.data
            val initScriptId = entry.savedStateHandle.get<UUID?>(ScriptCreatorArgs.RESULT_KEY)
            DashboardScreen(
                contentUri = deepLinkContentUri?.toString(),
                initScriptId = initScriptId,
                onCreateScriptRequest = {
                    navController.navigate(ScriptCreatorArgs)
                },
                onLaunchEditor = { script ->
                    navController.navigate(
                        EditorArgs(
                            script.text.split(" ", "\n").filterNot { it.isBlank() },
                        ),
                    )
                },
                onLaunchSettingsPage = {
                    navController.navigate(SettingArgs) {
                        launchSingleTop = true
                    }
                },
                onLaunchHistory = {
                    navController.navigate(HistoryArgs)
                },
            )
        }

        composable<EditorArgs> { entry ->
            EditorScreen(
                args = entry.toRoute(),
                onExportRequest = {
                    navController.navigate(ExportConfigSheetArgs)
                },
                onLaunchVoicePicker = {
                    navController.navigate(VoicePickerArgs(selectedVoiceId = it))
                },
            ) { pcm, audio ->
                // do nothing.
            }
        }

        composable<ScriptCreatorArgs> { entry ->
            ScriptCreatorScreen(onResult = {
                navController.popBackStack()
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set(ScriptCreatorArgs.RESULT_KEY, it?.id)
            })
        }

        composable<SettingArgs> {
            SettingScreen()
        }

        composable<VoicePickerArgs> { entry ->
            val args = entry.toRoute<VoicePickerArgs>()
            VoicePicker(
                selectedVoiceId = args.selectedVoiceId,
            ) {
                navController.popBackStack()
            }
        }

        bottomSheet<ExportConfigSheetArgs> {
            ExportConfigSheet(Modifier.fillMaxHeight(), onExport = {
                navController.navigate(ExportArgs(it))
            })
        }

        composable<ExportArgs> { entry ->
            ExportScreen(args = entry.toRoute(), onExit = {
                navController.popBackStack()
            })
        }

        composable<HistoryArgs> {
            HistoryScreen()
        }
    }
}