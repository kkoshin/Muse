package io.github.kkoshin.muse

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.kkoshin.muse.feature.dashboard.ScriptCreatorArgs
import io.github.kkoshin.muse.feature.dashboard.ScriptCreatorScreen

@Composable
internal fun MainScreen(navController: NavHostController = rememberNavController()) {
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = ScriptCreatorArgs,
    ) {
//        composable<DashboardArgs> { entry ->
//            DashboardScreen(
//                contentUri = null,
//                initScriptId = null,
//                onCreateScriptRequest = {
//                    navController.navigate(ScriptCreatorArgs)
//                },
//                onLaunchEditor = { script ->
//                    navController.navigate(
//                        EditorArgs(
//                            scriptId = script.id.toString(),
//                        ),
//                    )
//                },
//                onLaunchSettingsPage = {
//                    navController.navigate(SettingArgs) {
//                        launchSingleTop = true
//                    }
//                },
//                onDeepLinkHandled = {},
//                onLaunchAudioIsolation = { uri ->
////                    navController.navigate(
////                        AudioIsolationPreviewArgs(
////                            audioUri = uri,
////                        ),
////                    )
//                },
//                onLaunchWhiteNoise = {
////                    navController.navigate(WhiteNoiseConfigScreenArgs)
//                },
//            )
//        }

        composable<ScriptCreatorArgs> {
            ScriptCreatorScreen(onResult = { scriptId ->
                navController.popBackStack()
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set(ScriptCreatorArgs.RESULT_KEY, scriptId)
            })
        }
    }
}