package io.github.kkoshin.muse

import io.github.kkoshin.muse.core.manager.AccountManager
import io.github.kkoshin.muse.core.manager.ElevenLabProcessor
import io.github.kkoshin.muse.core.manager.SpeechProcessorManager
import io.github.kkoshin.muse.core.provider.AudioIsolationProvider
import io.github.kkoshin.muse.core.provider.STTProvider
import io.github.kkoshin.muse.core.provider.SoundEffectProvider
import io.github.kkoshin.muse.core.provider.TTSProvider
import io.github.kkoshin.muse.export.ExportViewModel
import io.github.kkoshin.muse.feature.dashboard.DashboardViewModel
import io.github.kkoshin.muse.feature.editor.EditorViewModel
import io.github.kkoshin.muse.isolation.AudioIsolationViewModel
import io.github.kkoshin.muse.noise.WhiteNoiseViewModel
import io.github.kkoshin.muse.repo.MuseRepo
import io.github.kkoshin.muse.stt.SttViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::MuseRepo)
    viewModelOf(::EditorViewModel)
    viewModel { ExportViewModel(get(), get(), get()) }
    viewModel { DashboardViewModel(get()) }
    viewModel { AudioIsolationViewModel(get()) }
    viewModel { SttViewModel(get()) }
    viewModel { WhiteNoiseViewModel(get()) }
    singleOf(::SpeechProcessorManager)
    singleOf(::AccountManager)
    single<CoroutineScope> { MainScope() }
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