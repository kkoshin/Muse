package io.github.kkoshin.muse

import io.github.kkoshin.muse.dashboard.DashboardViewModel
import io.github.kkoshin.muse.editor.EditorViewModel
import io.github.kkoshin.muse.export.ExportViewModel
import io.github.kkoshin.muse.isolation.AudioIsolationProvider
import io.github.kkoshin.muse.isolation.AudioIsolationViewModel
import io.github.kkoshin.muse.noise.SoundEffectProvider
import io.github.kkoshin.muse.noise.WhiteNoiseViewModel
import io.github.kkoshin.muse.repo.MuseRepo
import io.github.kkoshin.muse.stt.STTProvider
import io.github.kkoshin.muse.stt.SttViewModel
import io.github.kkoshin.muse.tts.TTSManager
import io.github.kkoshin.muse.tts.TTSProvider
import io.github.kkoshin.muse.tts.vendor.ElevenLabProvider
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
    singleOf(::TTSManager)
    singleOf(::AccountManager)
    single<CoroutineScope> { MainScope() }
    single<TTSProvider> {
        ElevenLabProvider(get(), get())
    }
    single<AudioIsolationProvider> {
        ElevenLabProvider(get(), get())
    }
    single<SoundEffectProvider> {
        ElevenLabProvider(get(), get())
    }
    single<STTProvider> {
        ElevenLabProvider(get(), get())
    }
}