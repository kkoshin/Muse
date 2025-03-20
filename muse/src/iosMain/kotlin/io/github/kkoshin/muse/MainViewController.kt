package io.github.kkoshin.muse

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController

//private val baseModule = module {
//    single<CoroutineScope> { MainScope() }
//    singleOf(::MuseRepo)
//    viewModelOf(::DashboardViewModel)
//}

fun MainViewController() = ComposeUIViewController {
//    KoinApplication(application = {
////        modules(baseModule)
//    }) {
//        MainScreen()
//    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("ScriptCreatorScreen")
    }
}

