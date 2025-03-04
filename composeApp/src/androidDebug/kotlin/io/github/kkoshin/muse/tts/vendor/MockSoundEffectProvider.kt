package io.github.kkoshin.muse.tts.vendor

import android.content.Context
import io.github.kkoshin.muse.debugLog
import io.github.kkoshin.muse.noise.SoundEffectConfig
import io.github.kkoshin.muse.noise.SoundEffectProvider
import kotlinx.coroutines.delay
import org.koin.java.KoinJavaComponent.inject
import java.io.OutputStream

class MockSoundEffectProvider : SoundEffectProvider {
    private val appContext: Context by inject(Context::class.java)
    override suspend fun makeSoundEffects(
        prompt: String,
        config: SoundEffectConfig,
        target: OutputStream
    ): Result<Unit> {
        debugLog { "start makeSoundEffects: $config" }
        delay(1000)
        return runCatching {
            appContext.assets.open("english.mp3").use { inputStream ->
                inputStream.copyTo(target)
            }
        }
    }
}