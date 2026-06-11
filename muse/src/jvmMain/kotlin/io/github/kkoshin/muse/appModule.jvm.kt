package io.github.kkoshin.muse

import io.github.kkoshin.muse.audio.AudioMetadataRetriever
import io.github.kkoshin.muse.core.manager.AccountManager
import io.github.kkoshin.muse.core.manager.ElevenLabProcessor
import io.github.kkoshin.muse.core.manager.ProcessorRouter
import io.github.kkoshin.muse.core.manager.SixtyDbProcessor
import io.github.kkoshin.muse.core.manager.SpeechProcessorManager
import io.github.kkoshin.muse.core.provider.AudioIsolationProvider
import io.github.kkoshin.muse.core.provider.STTProvider
import io.github.kkoshin.muse.core.provider.SoundEffectProvider
import io.github.kkoshin.muse.core.provider.TTSProvider
import io.github.kkoshin.muse.database.AppDatabase
import io.github.kkoshin.muse.feature.dashboard.DashboardViewModel
import io.github.kkoshin.muse.feature.editor.EditorViewModel
import io.github.kkoshin.muse.feature.export.ExportViewModel
import io.github.kkoshin.muse.feature.isolation.AudioIsolationViewModel
import io.github.kkoshin.muse.feature.noise.WhiteNoiseViewModel
import io.github.kkoshin.muse.platformbridge.MediaStoreHelper
import io.github.kkoshin.muse.platformbridge.ToastManager
import io.github.kkoshin.muse.repo.DriverFactory
import io.github.kkoshin.muse.repo.MusePathManager
import io.github.kkoshin.muse.repo.MuseRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import okio.Path

val appModule = module {
    single<MuseRepo> {
        val driver = DriverFactory().createDriver()
        AppDatabase.Schema.create(driver)
        MuseRepo(
            AppDatabase(driver),
            MusePathManager(),
        )
    }
    viewModelOf(::EditorViewModel)
    viewModelOf(::ExportViewModel)
    single { DashboardViewModel(get()) }
    single { AudioIsolationViewModel(get()) }
    single { WhiteNoiseViewModel(get()) }
    singleOf(::SpeechProcessorManager)
    singleOf(::MediaStoreHelper)
    single<AudioMetadataRetriever> {
        object : AudioMetadataRetriever {
            override suspend fun getAudioMetadata(path: Path): io.github.kkoshin.muse.audio.AudioMetadata? = null
        }
    }
    single<AccountManager> {
        // Mock AccountManager
        AccountManager(object : androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> {
            override val data: kotlinx.coroutines.flow.Flow<androidx.datastore.preferences.core.Preferences> = kotlinx.coroutines.flow.flowOf(androidx.datastore.preferences.core.emptyPreferences())
            override suspend fun updateData(transform: suspend (androidx.datastore.preferences.core.Preferences) -> androidx.datastore.preferences.core.Preferences): androidx.datastore.preferences.core.Preferences = androidx.datastore.preferences.core.emptyPreferences()
        })
    }
    single<CoroutineScope> { MainScope() }
    single<ToastManager> {
        object : ToastManager {
            override fun show(message: String?) {
                println("Toast: $message")
            }
        }
    }
    
    // Providers — same Router pattern as android/ios.
    single { ElevenLabProcessor(get(), get()) }
    single { SixtyDbProcessor(get(), get()) }
    single { ProcessorRouter(get(), get(), get()) }
    single<TTSProvider> { get<ProcessorRouter>() }
    single<AudioIsolationProvider> { get<ProcessorRouter>() }
    single<SoundEffectProvider> { get<ProcessorRouter>() }
    single<STTProvider> { get<ProcessorRouter>() }
}
