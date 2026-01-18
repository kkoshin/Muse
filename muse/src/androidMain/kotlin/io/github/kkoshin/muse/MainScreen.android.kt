package io.github.kkoshin.muse

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import io.github.kkoshin.muse.feature.dashboard.DashboardArgs
import io.github.kkoshin.muse.feature.editor.ExportConfigSheet
import io.github.kkoshin.muse.feature.editor.ExportConfigSheetArgs
import io.github.kkoshin.muse.feature.editor.ExportMode
import io.github.kkoshin.muse.feature.export.ExportArgs
import io.github.kkoshin.muse.feature.isolation.AudioIsolationArgs
import io.github.kkoshin.muse.feature.isolation.AudioIsolationPreviewArgs
import io.github.kkoshin.muse.feature.isolation.AudioIsolationPreviewScreen
import io.github.kkoshin.muse.feature.isolation.AudioIsolationScreen
import io.github.kkoshin.muse.feature.setting.OpenSourceArgs
import io.github.kkoshin.muse.feature.setting.OpenSourceScreen
import io.github.kkoshin.muse.platformbridge.toUri
import io.github.kkoshin.muse.workaround.bottomSheet
import okio.Path

actual fun NavGraphBuilder.addPlatformSpecificRoutes(navController: NavHostController) {
    bottomSheet<AudioIsolationPreviewArgs> { entry ->
        val args: AudioIsolationPreviewArgs = entry.toRoute()
        AudioIsolationPreviewScreen(args = args) {
            navController.navigate(AudioIsolationArgs(args.audioUri)) {
                popUpTo(DashboardArgs)
            }
        }
    }

    bottomSheet<ExportConfigSheetArgs> { entry ->
        val args: ExportConfigSheetArgs = entry.toRoute()
        ExportConfigSheet(
            Modifier.background(
                MaterialTheme.colors.background,
                shape = RoundedCornerShape(16.dp)
            ),
            voiceIds = args.voiceIds,
            voiceNames = args.voiceNames,
            mode = ExportMode.fromName(args.exportMode)!!,
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
                        args.exportMode,
                        fixedDurationEnabled,
                        fixedSilence,
                        silencePerChar,
                        minDynamicDuration,
                    ),
                )
            },
        )
    }

    composable<AudioIsolationArgs> { entry ->
        val args: AudioIsolationArgs = entry.toRoute()
        AudioIsolationScreen(args = args) {
            navController.popBackStack()
        }
    }

    composable<OpenSourceArgs> {
        OpenSourceScreen(onOpenURL = { url ->
            // Handled via platformInfo in SettingScreen
        })
    }
}

actual fun onLaunchAudioIsolation(navController: NavHostController, path: Path) {
    navController.navigate(
        AudioIsolationPreviewArgs(
            audioUri = path.toUri().toString(),
        ),
    )
}

actual fun onLaunchOpenSource(navController: NavHostController) {
    navController.navigate(OpenSourceArgs)
}
