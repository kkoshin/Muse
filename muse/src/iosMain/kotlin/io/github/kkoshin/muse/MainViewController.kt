package io.github.kkoshin.muse

import androidx.compose.ui.window.ComposeUIViewController
import io.github.kkoshin.muse.feature.dashboard.DashboardViewModel
import io.github.kkoshin.muse.repo.MuseRepo
import org.koin.compose.KoinApplication
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

private val baseModule = module {
//    single<CoroutineScope> { MainScope() }
    viewModel {
        DashboardViewModel(get())
    }
    singleOf(::MuseRepo)
}

fun MainViewController() = ComposeUIViewController {
    KoinApplication(application = {
        modules(baseModule)
    }) {
        MainScreen()
    }
}

