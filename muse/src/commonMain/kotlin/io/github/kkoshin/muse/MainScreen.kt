@file:OptIn(ExperimentalUuidApi::class)

package io.github.kkoshin.muse

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import io.github.kkoshin.muse.feature.dashboard.DashboardArgs
import io.github.kkoshin.muse.feature.dashboard.DashboardScreen
import io.github.kkoshin.muse.feature.dashboard.ScriptCreatorArgs
import io.github.kkoshin.muse.feature.dashboard.ScriptCreatorScreen
import io.github.kkoshin.muse.feature.editor.EditorArgs
import io.github.kkoshin.muse.feature.editor.EditorScreen
import io.github.kkoshin.muse.feature.editor.ExportConfigSheet
import io.github.kkoshin.muse.feature.editor.ExportConfigSheetArgs
import io.github.kkoshin.muse.feature.editor.ExportMode
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
import io.github.kkoshin.muse.feature.setting.ApiKeyHelpArgs
import io.github.kkoshin.muse.feature.setting.ApiKeyHelpScreen
import io.github.kkoshin.muse.feature.setting.OpenSourceArgs
import io.github.kkoshin.muse.feature.setting.OpenSourceScreen
import io.github.kkoshin.muse.feature.setting.SettingArgs
import io.github.kkoshin.muse.feature.setting.SettingScreen
import io.github.kkoshin.muse.feature.setting.voice.VoicePicker
import io.github.kkoshin.muse.feature.setting.voice.VoicePickerArgs
import io.github.kkoshin.muse.navigation.BottomSheetSceneStrategy
import io.github.kkoshin.muse.navigation.bottomSheetMetadata
import io.github.kkoshin.muse.platformbridge.toNavRouteString
import io.github.kkoshin.muse.platformbridge.rememberPlatformSpecificInfo
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlin.uuid.ExperimentalUuidApi

private val fadeTransition: (AnimatedContentTransitionScope<Scene<NavKey>>) -> ContentTransform = {
    ContentTransform(
        targetContentEnter = fadeIn(tween(300)),
        initialContentExit = fadeOut(tween(300)),
        sizeTransform = SizeTransform(false),
    )
}

private val fadePopTransition: (AnimatedContentTransitionScope<Scene<NavKey>>, Int) -> ContentTransform = { _, _ ->
    ContentTransform(
        targetContentEnter = fadeIn(tween(300)),
        initialContentExit = fadeOut(tween(300)),
        sizeTransform = SizeTransform(false),
    )
}

private val navConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(DashboardArgs::class, DashboardArgs.serializer())
            subclass(ScriptCreatorArgs::class, ScriptCreatorArgs.serializer())
            subclass(EditorArgs::class, EditorArgs.serializer())
            subclass(ExportConfigSheetArgs::class, ExportConfigSheetArgs.serializer())
            subclass(ExportArgs::class, ExportArgs.serializer())
            subclass(SettingArgs::class, SettingArgs.serializer())
            subclass(OpenSourceArgs::class, OpenSourceArgs.serializer())
            subclass(VoicePickerArgs::class, VoicePickerArgs.serializer())
            subclass(WhiteNoiseConfigScreenArgs::class, WhiteNoiseConfigScreenArgs.serializer())
            subclass(WhiteNoiseScreenArgs::class, WhiteNoiseScreenArgs.serializer())
            subclass(AudioIsolationPreviewArgs::class, AudioIsolationPreviewArgs.serializer())
            subclass(AudioIsolationArgs::class, AudioIsolationArgs.serializer())
            subclass(ApiKeyHelpArgs::class, ApiKeyHelpArgs.serializer())
        }
    }
}

@Composable
fun MainScreen() {
    val backStack = rememberNavBackStack(navConfig, DashboardArgs)
    val platformInfo = rememberPlatformSpecificInfo()
    val bottomSheetStrategy = remember { BottomSheetSceneStrategy<NavKey>() }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        sceneStrategy = bottomSheetStrategy.then(SinglePaneSceneStrategy()),
        transitionSpec = fadeTransition,
        popTransitionSpec = fadeTransition,
        predictivePopTransitionSpec = fadePopTransition,
        entryProvider = { key ->
            when (key) {
                is DashboardArgs -> NavEntry(key) {
                    DashboardScreen(
                        initScriptId = null,
                        onLaunchEditor = { script ->
                            backStack.add(EditorArgs(scriptId = script.id.toString()))
                        },
                        onCreateScriptRequest = {
                            backStack.add(ScriptCreatorArgs)
                        },
                        onLaunchSettingsPage = {
                            if (backStack.none { it is SettingArgs }) {
                                backStack.add(SettingArgs())
                            }
                        },
                        onLaunchAudioIsolation = { path ->
                            backStack.add(
                                AudioIsolationPreviewArgs(audioUri = path.toNavRouteString()),
                            )
                        },
                        onLaunchWhiteNoise = {
                            backStack.add(WhiteNoiseConfigScreenArgs)
                        },
                    )
                }

                is EditorArgs -> NavEntry(key) {
                    EditorScreen(
                        args = key,
                        onExportRequest = { voices, mode ->
                            backStack.add(
                                voices.associate { it.voiceId to it.name }.let {
                                    ExportConfigSheetArgs(
                                        voiceIds = it.keys.toList(),
                                        voiceNames = it.values.toList(),
                                        scriptId = key.scriptId,
                                        exportMode = mode.name,
                                    )
                                },
                            )
                        },
                        onPickVoice = {
                            backStack.add(VoicePickerArgs(emptyList()))
                        },
                        onNavigateToSettings = {
                            backStack.add(SettingArgs(scrollToApiKey = true))
                        },
                        onNavigateToApiKeyHelp = {
                            backStack.add(ApiKeyHelpArgs)
                        },
                    )
                }

                is ExportArgs -> NavEntry(key) {
                    ExportScreen(args = key, onExit = { isSuccess ->
                        if (isSuccess) {
                            backStack.removeAll { it !is DashboardArgs }
                        } else {
                            backStack.removeLastOrNull()
                        }
                    })
                }

                is ScriptCreatorArgs -> NavEntry(key) {
                    ScriptCreatorScreen(onResult = {
                        backStack.removeLastOrNull()
                    })
                }

                is VoicePickerArgs -> NavEntry(key) {
                    VoicePicker(selectedVoiceIds = key.selectedVoiceIds.toSet()) {
                        backStack.removeLastOrNull()
                    }
                }

                is SettingArgs -> NavEntry(key) {
                    SettingScreen(
                        args = key,
                        versionName = platformInfo.versionName,
                        versionCode = platformInfo.versionCode,
                        folderPath = platformInfo.exportFolderPath,
                        onLaunchVoiceScreen = {
                            backStack.add(VoicePickerArgs(it.toList()))
                        },
                        onLaunchOpenSourceScreen = {
                            backStack.add(OpenSourceArgs)
                        },
                        onOpenURL = { url ->
                            platformInfo.onOpenURL(url)
                        },
                    )
                }

                is ApiKeyHelpArgs -> NavEntry(key) {
                    ApiKeyHelpScreen(
                        onGoToSettings = {
                            backStack.add(SettingArgs(scrollToApiKey = true))
                        },
                        onOpenURL = { platformInfo.onOpenURL(it) },
                    )
                }

                is OpenSourceArgs -> NavEntry(key) {
                    OpenSourceScreen(onOpenURL = { platformInfo.onOpenURL(it) })
                }

                is WhiteNoiseConfigScreenArgs -> NavEntry(key) {
                    WhiteNoiseConfigScreen(
                        onGenerate = { prompt, config ->
                            backStack.add(
                                WhiteNoiseScreenArgs(
                                    prompt,
                                    config.duration?.inWholeMilliseconds,
                                    config.promptInfluence,
                                ),
                            )
                        },
                        onNavigateToSettings = {
                            backStack.add(SettingArgs(scrollToApiKey = true))
                        },
                        onNavigateToApiKeyHelp = {
                            backStack.add(ApiKeyHelpArgs)
                        },
                    )
                }

                is WhiteNoiseScreenArgs -> NavEntry(key) {
                    WhiteNoiseScreen(args = key) { isSuccess ->
                        if (isSuccess) {
                            backStack.removeAll { it !is DashboardArgs }
                        } else {
                            backStack.removeLastOrNull()
                        }
                    }
                }

                // Bottom sheet entries — marked with metadata
                is AudioIsolationPreviewArgs -> NavEntry(key, metadata = bottomSheetMetadata()) {
                    AudioIsolationPreviewScreen(args = key) {
                        backStack.removeAll { it is AudioIsolationPreviewArgs }
                        backStack.add(AudioIsolationArgs(key.audioUri))
                    }
                }

                is ExportConfigSheetArgs -> NavEntry(key, metadata = bottomSheetMetadata()) {
                    ExportConfigSheet(
                        voiceIds = key.voiceIds,
                        voiceNames = key.voiceNames,
                        mode = ExportMode.fromName(key.exportMode) ?: ExportMode.Reading,
                        onExport = { voiceId, fixedDurationEnabled, fixedSilence, silencePerChar, minDynamicDuration ->
                            backStack.add(
                                ExportArgs(
                                    voiceId,
                                    key.scriptId,
                                    key.exportMode,
                                    fixedDurationEnabled,
                                    fixedSilence,
                                    silencePerChar,
                                    minDynamicDuration,
                                ),
                            )
                        },
                    )
                }

                is AudioIsolationArgs -> NavEntry(key) {
                    AudioIsolationScreen(
                        args = key,
                        onDone = { backStack.removeLastOrNull() },
                        onNavigateToSettings = {
                            backStack.add(SettingArgs(scrollToApiKey = true))
                        },
                        onNavigateToApiKeyHelp = {
                            backStack.add(ApiKeyHelpArgs)
                        },
                    )
                }

                else -> error("Unknown route: $key")
            }
        },
    )
}
