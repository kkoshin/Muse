@file:OptIn(ExperimentalUuidApi::class)

package io.github.kkoshin.muse

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.kkoshin.muse.feature.dashboard.DashboardArgs
import io.github.kkoshin.muse.feature.dashboard.DashboardScreen
import io.github.kkoshin.muse.feature.dashboard.ScriptCreatorArgs
import io.github.kkoshin.muse.feature.dashboard.ScriptCreatorArgs.setScriptId
import io.github.kkoshin.muse.feature.dashboard.ScriptCreatorScreen
import kotlin.uuid.ExperimentalUuidApi

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
            composable<DashboardArgs> { entry ->
                DashboardScreen(
                    initScriptId = null,
                    onLaunchEditor = { script ->
//                    navController.navigate(
//                        EditorArgs(
//                            scriptId = script.id.toString(),
//                        ),
//                    )
                    },
                    onCreateScriptRequest = {
                        navController.navigate(ScriptCreatorArgs)
                    },
                    onLaunchSettingsPage = {
//                    navController.navigate(SettingArgs) {
//                        launchSingleTop = true
//                    }
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

            composable<ScriptCreatorArgs> {
                ScriptCreatorScreen(onResult = { scriptId ->
                    navController.popBackStack()
                    navController.currentBackStackEntry
                        ?.savedStateHandle?.setScriptId(scriptId)
                })
            }
        }
    }
}