package io.github.kkoshin.muse

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
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
import io.github.kkoshin.muse.setting.OpenSourceArgs
import io.github.kkoshin.muse.setting.OpenSourceScreen
import io.github.kkoshin.muse.setting.SettingArgs
import io.github.kkoshin.muse.setting.SettingScreen
import io.github.kkoshin.muse.setting.voice.VoicePicker
import io.github.kkoshin.muse.setting.voice.VoicePickerArgs
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
                            scriptId = script.id.toString(),
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
            val args = entry.toRoute<EditorArgs>()
            EditorScreen(
                args = args,
                onExportRequest = { voices ->
                    navController.navigate(
                        voices.associate { it.voiceId to it.name }.let {
                            ExportConfigSheetArgs(
                                voiceIds = it.keys.toList(),
                                voiceNames = it.values.toList(),
                                scriptId = args.scriptId,
                            )
                        },
                    )
                },
                onPickVoice = {
                    navController.navigate(VoicePickerArgs(emptyList()))
                },
            )
        }

        composable<ScriptCreatorArgs> { entry ->
            ScriptCreatorScreen(onResult = { scriptId ->
                navController.popBackStack()
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set(ScriptCreatorArgs.RESULT_KEY, scriptId)
            })
        }

        composable<SettingArgs> {
            SettingScreen(onLaunchVoiceScreen = {
                navController.navigate(VoicePickerArgs(it.toList()))
            }, onLaunchOpenSourceScreen = {
                navController.navigate(OpenSourceArgs)
            })
        }

        composable<VoicePickerArgs> { entry ->
            val args = entry.toRoute<VoicePickerArgs>()
            VoicePicker(selectedVoiceIds = args.selectedVoiceIds.toSet()) {
                navController.popBackStack()
            }
        }

        bottomSheet<ExportConfigSheetArgs> { entry ->
            val args: ExportConfigSheetArgs = entry.toRoute()
            ExportConfigSheet(
                Modifier,
                voiceIds = args.voiceIds,
                voiceNames = args.voiceNames,
                onExport = {
                        voiceId,
                        fixedDurationEnabled,
                        fixedSilence,
                        silencePerChar,
                        minDynamicDuration,
                    ->
                    navController.navigate(
                        ExportArgs(
                            voiceId,
                            args.scriptId,
                            fixedDurationEnabled,
                            fixedSilence,
                            silencePerChar,
                            minDynamicDuration,
                        ),
                    )
                },
            )
        }

        composable<ExportArgs> { entry ->
            ExportScreen(args = entry.toRoute(), onExit = { isSuccess ->
                if (isSuccess) {
                    navController.popBackStack(DashboardArgs, false)
                } else {
                    navController.popBackStack()
                }
            })
        }

        composable<HistoryArgs> {
            HistoryScreen()
        }

        composable<OpenSourceArgs> {
            OpenSourceScreen()
        }

        // TODO: 初次使用需要配置 elvenlabs api key
    }
}