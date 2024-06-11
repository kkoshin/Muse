package com.github.kkoshin.muse

import android.app.Application
import com.github.kkoshin.muse.editor.EditorViewModel
import com.github.kkoshin.muse.tts.TTSManager
import com.github.kkoshin.muse.tts.TTSProvider
import com.github.kkoshin.muse.tts.vendor.MockTTSProvider
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class App : Application() {
    private val appModule = module {
        single<TTSProvider> {
            MockTTSProvider()
//            ElevenLabTTSProvider()
        }
        singleOf(::MuseRepo)
        viewModel { EditorViewModel(get(), get()) }
        singleOf(::TTSManager)
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