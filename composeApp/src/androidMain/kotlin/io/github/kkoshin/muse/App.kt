package io.github.kkoshin.muse

import android.app.Application
import io.github.kkoshin.muse.dashboard.DashboardViewModel
import io.github.kkoshin.muse.editor.EditorViewModel
import io.github.kkoshin.muse.export.ExportViewModel
import io.github.kkoshin.muse.repo.MuseRepo
import io.github.kkoshin.muse.tts.TTSManager
import io.github.kkoshin.muse.tts.TTSProvider
import io.github.kkoshin.muse.tts.vendor.ElevenLabTTSProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class App : Application() {
    private val appModule = module {
        singleOf(::MuseRepo)
        viewModelOf(::EditorViewModel)
        viewModel { ExportViewModel(get(), get()) }
        viewModel { DashboardViewModel(get()) }
        singleOf(::TTSManager)
        singleOf(::AccountManager)
        single<CoroutineScope> { MainScope() }
        single<TTSProvider> {
            ElevenLabTTSProvider(get(), get())
        }
    }

    override fun onCreate() {
        super.onCreate()
        AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }
}