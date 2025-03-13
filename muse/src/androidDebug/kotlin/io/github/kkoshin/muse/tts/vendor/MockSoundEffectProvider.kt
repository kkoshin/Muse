package io.github.kkoshin.muse.tts.vendor

import android.content.Context
import io.github.kkoshin.muse.core.provider.SoundEffectConfig
import io.github.kkoshin.muse.core.provider.SoundEffectProvider
import io.github.kkoshin.muse.debugLog
import kotlinx.coroutines.delay
import okio.Sink
import okio.buffer
import okio.source
import org.koin.java.KoinJavaComponent.inject

class MockSoundEffectProvider : SoundEffectProvider {
    private val appContext: Context by inject(Context::class.java)
    override suspend fun makeSoundEffects(
        prompt: String,
        config: SoundEffectConfig,
        target: Sink
    ): Result<Unit> {
        debugLog { "start makeSoundEffects: $config" }
        delay(1000)
        return runCatching {
            appContext.assets.open("english.mp3").use { inputStream ->
                target.buffer().use {
                    it.writeAll(inputStream.source())
                }
            }
        }
    }
}