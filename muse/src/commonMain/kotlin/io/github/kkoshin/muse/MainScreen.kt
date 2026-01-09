@file:OptIn(ExperimentalUuidApi::class)

package io.github.kkoshin.muse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.github.kkoshin.muse.feature.dashboard.DashboardArgs
import io.github.kkoshin.muse.feature.dashboard.DashboardScreen
import io.github.kkoshin.muse.feature.dashboard.ScriptCreatorArgs
import io.github.kkoshin.muse.feature.dashboard.ScriptCreatorArgs.setScriptId
import io.github.kkoshin.muse.feature.dashboard.ScriptCreatorScreen
import io.github.kkoshin.muse.feature.editor.EditorArgs
import io.github.kkoshin.muse.feature.editor.EditorScreen
import io.github.kkoshin.muse.feature.editor.ExportConfigSheet
import io.github.kkoshin.muse.feature.editor.ExportConfigSheetArgs
import io.github.kkoshin.muse.feature.export.ExportArgs
import io.github.kkoshin.muse.feature.export.ExportScreen
import io.github.kkoshin.muse.feature.noise.WhiteNoiseConfigScreen
import io.github.kkoshin.muse.feature.noise.WhiteNoiseConfigScreenArgs
import io.github.kkoshin.muse.feature.noise.WhiteNoiseScreen
import io.github.kkoshin.muse.feature.noise.WhiteNoiseScreenArgs
import io.github.kkoshin.muse.feature.setting.SettingArgs
import io.github.kkoshin.muse.feature.setting.SettingScreen
import io.github.kkoshin.muse.feature.setting.voice.VoicePicker
import io.github.kkoshin.muse.feature.setting.voice.VoicePickerArgs
import io.github.kkoshin.muse.platformbridge.PlatformSpecificInfo
import io.github.kkoshin.muse.platformbridge.rememberPlatformSpecificInfo
import androidx.navigation.NavGraphBuilder
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun MainScreen(navController: NavHostController = rememberNavController()) {
    val platformInfo = rememberPlatformSpecificInfo()
    
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
                        onLaunchAudioIsolation(navController, uri)
                    },
                    onLaunchWhiteNoise = {
                        navController.navigate(WhiteNoiseConfigScreenArgs)
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

            // TODO: 待 navigation 支持 bottom sheet 后调整
            dialog<ExportConfigSheetArgs> { entry ->
                val args: ExportConfigSheetArgs = entry.toRoute()
                ExportConfigSheet(
                    Modifier.background(
                        MaterialTheme.colors.background,
                        shape = RoundedCornerShape(16.dp)
                    ),
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
                SettingScreen(
                    versionName = platformInfo.versionName,
                    versionCode = platformInfo.versionCode,
                    folderPath = platformInfo.exportFolderPath,
                    onLaunchVoiceScreen = {
                        navController.navigate(VoicePickerArgs(it.toList()))
                    },
                    onLaunchOpenSourceScreen = {
                        onLaunchOpenSource(navController)
                    },
                    onOpenURL = { url ->
                        platformInfo.onOpenURL(url)
                    }
                )
            }

            composable<WhiteNoiseConfigScreenArgs> {
                WhiteNoiseConfigScreen { prompt, config ->
                    navController.navigate(
                        WhiteNoiseScreenArgs(
                            prompt,
                            config.duration?.inWholeMilliseconds,
                            config.promptInfluence
                        )
                    )
                }
            }

            composable<WhiteNoiseScreenArgs> { entry ->
                WhiteNoiseScreen(args = entry.toRoute()) { isSuccess ->
                    if (isSuccess) {
                        navController.popBackStack(DashboardArgs, false)
                    } else {
                        navController.popBackStack()
                    }
                }
            }
            
            addPlatformSpecificRoutes(navController)
        }
    }
}

expect fun NavGraphBuilder.addPlatformSpecificRoutes(navController: NavHostController)

expect fun onLaunchAudioIsolation(navController: NavHostController, path: okio.Path)

expect fun onLaunchOpenSource(navController: NavHostController)

