package io.github.kkoshin.muse

import io.github.kkoshin.muse.core.manager.ElevenLabProcessor
import io.github.kkoshin.muse.core.provider.AudioIsolationProvider
import io.github.kkoshin.muse.core.provider.STTProvider
import io.github.kkoshin.muse.core.provider.SoundEffectProvider
import io.github.kkoshin.muse.core.provider.TTSProvider
import io.github.kkoshin.muse.tts.vendor.MockAudioIsolationProvider
import io.github.kkoshin.muse.tts.vendor.MockSoundEffectProvider
import io.github.kkoshin.muse.tts.vendor.MockTTSProvider
import org.koin.dsl.module

val mockAppModule = module {
    includes(baseModule)
    single<TTSProvider> {
        MockTTSProvider()
    }
    single<AudioIsolationProvider> {
        MockAudioIsolationProvider()
    }
    single<SoundEffectProvider> {
        MockSoundEffectProvider()
    }
    single<STTProvider> {
        ElevenLabProcessor(get(), get())
    }
}