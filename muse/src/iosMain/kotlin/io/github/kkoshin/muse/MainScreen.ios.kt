package io.github.kkoshin.muse

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import okio.Path

actual fun NavGraphBuilder.addPlatformSpecificRoutes(navController: NavHostController) {
    // TODO: Implement parity routes for iOS
}

actual fun onLaunchAudioIsolation(navController: NavHostController, path: Path) {
    // TODO: Implement for iOS
}

actual fun onLaunchOpenSource(navController: NavHostController) {
    // TODO: Implement for iOS
}
