@file:OptIn(ExperimentalUuidApi::class)

package io.github.kkoshin.muse.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.github.foodiestudio.sugar.storage.filesystem.toOkioPath
import io.github.kkoshin.muse.feature.dashboard.DashboardArgs
import io.github.kkoshin.muse.feature.dashboard.DashboardScreen
import io.github.kkoshin.muse.feature.dashboard.ScriptCreatorArgs
import io.github.kkoshin.muse.feature.dashboard.ScriptCreatorArgs.getScriptId
import io.github.kkoshin.muse.feature.dashboard.ScriptCreatorArgs.setScriptId
import io.github.kkoshin.muse.feature.dashboard.ScriptCreatorScreen
import io.github.kkoshin.muse.feature.editor.EditorArgs
import io.github.kkoshin.muse.feature.editor.EditorScreen
import io.github.kkoshin.muse.feature.editor.ExportConfigSheet
import io.github.kkoshin.muse.feature.editor.ExportConfigSheetArgs
import io.github.kkoshin.muse.feature.export.ExportArgs
import io.github.kkoshin.muse.feature.export.ExportScreen
import io.github.kkoshin.muse.feature.isolation.AudioIsolationArgs
import io.github.kkoshin.muse.feature.isolation.AudioIsolationPreviewArgs
import io.github.kkoshin.muse.feature.isolation.AudioIsolationPreviewScreen
import io.github.kkoshin.muse.feature.isolation.AudioIsolationScreen
import io.github.kkoshin.muse.feature.noise.WhiteNoiseConfigScreen
import io.github.kkoshin.muse.feature.noise.WhiteNoiseConfigScreenArgs
import io.github.kkoshin.muse.feature.noise.WhiteNoiseScreen
import io.github.kkoshin.muse.feature.noise.WhiteNoiseScreenArgs
import io.github.kkoshin.muse.feature.setting.OpenSourceArgs
import io.github.kkoshin.muse.feature.setting.OpenSourceScreen
import io.github.kkoshin.muse.feature.setting.SettingArgs
import io.github.kkoshin.muse.feature.setting.SettingScreen
import io.github.kkoshin.muse.feature.setting.voice.VoicePicker
import io.github.kkoshin.muse.feature.setting.voice.VoicePickerArgs
import io.github.kkoshin.muse.feature.stt.SttArgs
import io.github.kkoshin.muse.feature.stt.SttScreen
import io.github.kkoshin.muse.workaround.bottomSheet
import org.koin.androidx.compose.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

private fun Bundle.getDeepLinkUri(): Uri? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(NavController.KEY_DEEP_LINK_INTENT, Intent::class.java)?.data
    } else {
        @Suppress("DEPRECATION")
        getParcelable<Intent>(NavController.KEY_DEEP_LINK_INTENT)?.data
    }

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
            // 需使用 rememberSaveable 而非 remember, 避免重复处理相同的 deeplink
            var deeplinkUri: Uri? by rememberSaveable(entry) {
                mutableStateOf(entry.arguments?.getDeepLinkUri())
            }
            val initScriptId = entry.savedStateHandle.getScriptId()
            DashboardScreen(
                contentUri = deeplinkUri?.toOkioPath(),
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
                onDeepLinkHandled = {
                    deeplinkUri = null
                },
                onLaunchAudioIsolation = { uri ->
                    navController.navigate(
                        AudioIsolationPreviewArgs(
                            audioUri = uri,
                        ),
                    )
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
                viewModel = koinViewModel(),
            )
        }

        composable<ScriptCreatorArgs> {
            ScriptCreatorScreen(onResult = { scriptId ->
                navController.popBackStack()
                navController.currentBackStackEntry
                    ?.savedStateHandle?.setScriptId(scriptId)
            })
        }

        composable<SettingArgs> {
            SettingScreen(
                versionName = BuildConfig.VERSION_NAME,
                versionCode = BuildConfig.VERSION_CODE,
                onLaunchVoiceScreen = {
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

        bottomSheet<AudioIsolationPreviewArgs> { entry ->
            val args: AudioIsolationPreviewArgs = entry.toRoute()
            AudioIsolationPreviewScreen(args = args) {
                navController.navigate(AudioIsolationArgs(args.audioUri)) {
                    popUpTo(DashboardArgs)
                }
            }
        }

        composable<AudioIsolationArgs> { entry ->
            val args: AudioIsolationArgs = entry.toRoute()
            AudioIsolationScreen(args = args) {
                navController.popBackStack()
            }
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

        composable<OpenSourceArgs> {
            OpenSourceScreen()
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

        composable<SttArgs> { entry ->
            SttScreen(args = entry.toRoute())
        }
    }
}