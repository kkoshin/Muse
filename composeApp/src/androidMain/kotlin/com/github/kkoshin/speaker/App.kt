package com.github.kkoshin.speaker

import android.app.Application
import com.github.kkoshin.speaker.editor.EditorViewModel
import com.github.kkoshin.speaker.tts.TTSManager
import com.github.kkoshin.speaker.tts.TTSProvider
import com.github.kkoshin.speaker.tts.vendor.ElevenLabTTSProvider
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
            ElevenLabTTSProvider()
        }
        viewModel { EditorViewModel(get()) }
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