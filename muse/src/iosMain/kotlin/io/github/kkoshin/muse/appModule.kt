package io.github.kkoshin.muse

import io.github.kkoshin.muse.database.AppDatabase
import io.github.kkoshin.muse.feature.dashboard.DashboardViewModel
import io.github.kkoshin.muse.platformbridge.ToastManager
import io.github.kkoshin.muse.repo.DriverFactory
import io.github.kkoshin.muse.repo.MusePathManager
import io.github.kkoshin.muse.repo.MuseRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<CoroutineScope> { MainScope() }
    viewModel {
        DashboardViewModel(get())
    }
    single<MuseRepo> {
        MuseRepo(
            AppDatabase(DriverFactory().createDriver()),
            MusePathManager(),
        )
    }
    single<ToastManager> {
        object: ToastManager {
            override fun show(message: String) {
                // TODO: Implement this
                println(message)
            }
        }
    }
}