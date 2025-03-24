package io.github.kkoshin.muse

import io.github.kkoshin.muse.feature.dashboard.DashboardViewModel
import io.github.kkoshin.muse.platformbridge.ToastManager
import io.github.kkoshin.muse.repo.MuseRepo
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
//    single<CoroutineScope> { MainScope() }
    viewModel {
        DashboardViewModel(get())
    }
    singleOf(::MuseRepo)
    single<ToastManager> {
        object: ToastManager {
            override fun show(message: String) {
                TODO()
            }
        }
    }
}