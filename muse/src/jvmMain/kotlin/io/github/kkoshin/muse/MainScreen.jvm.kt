package io.github.kkoshin.muse

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

actual fun NavGraphBuilder.addPlatformSpecificRoutes(navController: NavHostController) {
    // No-op for Desktop
}

actual fun onLaunchAudioIsolation(navController: NavHostController, path: okio.Path) {
    // No-op for Desktop
}

actual fun onLaunchOpenSource(navController: NavHostController) {
    // No-op for Desktop
}
