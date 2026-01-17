package io.github.kkoshin.muse

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import io.github.kkoshin.muse.feature.editor.ExportConfigSheet
import io.github.kkoshin.muse.feature.editor.ExportConfigSheetArgs
import io.github.kkoshin.muse.feature.export.ExportArgs
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import okio.Path

actual fun NavGraphBuilder.addPlatformSpecificRoutes(navController: NavHostController) {
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
}

actual fun onLaunchAudioIsolation(navController: NavHostController, path: Path) {
    // TODO: Implement for iOS
}

actual fun onLaunchOpenSource(navController: NavHostController) {
    // TODO: Implement for iOS
}
