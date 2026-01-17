package io.github.kkoshin.muse

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController


interface LocalNavController {
    fun navigateUp()
}

val LocalNavigationController = staticCompositionLocalOf<LocalNavController> {
    TODO("Not yet implemented")
}

class LocalNavControllerImpl(private val navController: NavHostController) : LocalNavController {
    override fun navigateUp() {
        navController.navigateUp()
    }
}