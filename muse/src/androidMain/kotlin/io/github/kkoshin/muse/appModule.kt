package io.github.kkoshin.muse

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import io.github.kkoshin.muse.core.manager.AccountManager
import io.github.kkoshin.muse.core.manager.ElevenLabProcessor
import io.github.kkoshin.muse.core.manager.SpeechProcessorManager
import io.github.kkoshin.muse.core.provider.AudioIsolationProvider
import io.github.kkoshin.muse.core.provider.STTProvider
import io.github.kkoshin.muse.core.provider.SoundEffectProvider
import io.github.kkoshin.muse.core.provider.TTSProvider
import io.github.kkoshin.muse.feature.dashboard.DashboardViewModel
import io.github.kkoshin.muse.feature.editor.EditorViewModel
import io.github.kkoshin.muse.feature.export.ExportViewModel
import io.github.kkoshin.muse.feature.isolation.AudioIsolationViewModel
import io.github.kkoshin.muse.feature.noise.WhiteNoiseViewModel
import io.github.kkoshin.muse.feature.stt.SttViewModel
import io.github.kkoshin.muse.repo.MuseRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

private val Context.accountDataStore: DataStore<Preferences> by preferencesDataStore(name = "account")

internal val baseModule = module {
    singleOf(::MuseRepo)
    viewModelOf(::EditorViewModel)
    viewModel { ExportViewModel(get(), get(), get()) }
    viewModel { DashboardViewModel(get()) }
    viewModel { AudioIsolationViewModel(get()) }
    viewModel { SttViewModel(get()) }
    viewModel { WhiteNoiseViewModel(get()) }
    singleOf(::SpeechProcessorManager)
    single<AccountManager> {
        val context = get<Context>()
        AccountManager(context.accountDataStore)
    }
    single<CoroutineScope> { MainScope() }
}

val appModule = module {
    includes(baseModule)
    single<TTSProvider> {
        ElevenLabProcessor(get(), get())
    }
    single<AudioIsolationProvider> {
        ElevenLabProcessor(get(), get())
    }
    single<SoundEffectProvider> {
        ElevenLabProcessor(get(), get())
    }
    single<STTProvider> {
        ElevenLabProcessor(get(), get())
    }
}