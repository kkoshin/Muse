package io.github.kkoshin.muse

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.uikit.LocalUIViewController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.github.kkoshin.muse.feature.dashboard.DashboardArgs
import io.github.kkoshin.muse.feature.dashboard.DashboardScreen
import io.github.kkoshin.muse.feature.dashboard.ScriptCreatorArgs
import io.github.kkoshin.muse.feature.dashboard.ScriptCreatorArgs.setScriptId
import io.github.kkoshin.muse.feature.dashboard.ScriptCreatorScreen
import io.github.kkoshin.muse.feature.editor.EditorArgs
import io.github.kkoshin.muse.feature.editor.EditorScreen
import io.github.kkoshin.muse.feature.editor.ExportConfigSheetArgs
import io.github.kkoshin.muse.feature.setting.SettingArgs
import io.github.kkoshin.muse.feature.setting.SettingScreen
import io.github.kkoshin.muse.feature.setting.voice.VoicePicker
import io.github.kkoshin.muse.feature.setting.voice.VoicePickerArgs
import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.SafariServices.SFSafariViewController
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
internal fun MainScreen(navController: NavHostController = rememberNavController()) {
    CompositionLocalProvider(
        LocalNavigationController provides LocalNavControllerImpl(navController)
    ) {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = DashboardArgs,
        ) {
            composable<DashboardArgs> { _ ->
                DashboardScreen(
                    initScriptId = null,
                    onLaunchEditor = { script ->
                        navController.navigate(
                            EditorArgs(
                                scriptId = script.id.toString(),
                            ),
                        )
                    },
                    onCreateScriptRequest = {
                        navController.navigate(ScriptCreatorArgs)
                    },
                    onLaunchSettingsPage = {
                        navController.navigate(SettingArgs) {
                            launchSingleTop = true
                        }
                    },
                    onLaunchAudioIsolation = { uri ->
//                    navController.navigate(
//                        AudioIsolationPreviewArgs(
//                            audioUri = uri,
//                        ),
//                    )
                    },
                    onLaunchWhiteNoise = {
//                    navController.navigate(WhiteNoiseConfigScreenArgs)
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

            composable<ScriptCreatorArgs> {
                ScriptCreatorScreen(onResult = { scriptId ->
                    navController.popBackStack()
                    navController.currentBackStackEntry
                        ?.savedStateHandle?.setScriptId(scriptId)
                })
            }

            composable<VoicePickerArgs> { entry ->
                val args = entry.toRoute<VoicePickerArgs>()
                VoicePicker(selectedVoiceIds = args.selectedVoiceIds.toSet()) {
                    navController.popBackStack()
                }
            }

            composable<SettingArgs> {
                val viewController = LocalUIViewController.current

                SettingScreen(
                    versionName = NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString")
                        ?.toString() ?: "Unknown",
                    versionCode = NSBundle.mainBundle.infoDictionary?.get("CFBundleVersion")
                        ?.toString()?.toInt()
                        ?: 0,
                    folderPath = "TODO", // TODO: Implement SettingScreen
                    onLaunchVoiceScreen = {
                        navController.navigate(VoicePickerArgs(it.toList()))
                    },
                    onLaunchOpenSourceScreen = {
//                        navController.navigate(OpenSourceArgs)
                    },
                    onOpenURL = { url ->
                        viewController.presentViewController(
                            SFSafariViewController(NSURL.URLWithString(url)!!),
                            animated = true,
                            null
                        )
                    }
                )
            }
        }
    }
}