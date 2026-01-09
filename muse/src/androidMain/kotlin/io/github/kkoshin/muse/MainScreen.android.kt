package io.github.kkoshin.muse

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import io.github.kkoshin.muse.feature.isolation.AudioIsolationArgs
import io.github.kkoshin.muse.feature.isolation.AudioIsolationPreviewArgs
import io.github.kkoshin.muse.feature.isolation.AudioIsolationPreviewScreen
import io.github.kkoshin.muse.feature.isolation.AudioIsolationScreen
import io.github.kkoshin.muse.feature.setting.OpenSourceArgs
import io.github.kkoshin.muse.feature.setting.OpenSourceScreen
import io.github.kkoshin.muse.feature.dashboard.DashboardArgs
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
