package com.github.kkoshin.muse.tts.vendor

import android.content.Context
import com.github.kkoshin.muse.tts.SupportedAudioType
import com.github.kkoshin.muse.tts.TTSProvider
import com.github.kkoshin.muse.tts.TTSResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class MockTTSProvider : TTSProvider {
    private val appContext: Context by inject(Context::class.java)

    override suspend fun generate(text: String): Result<TTSResult> {
        delay(1000)
        return runCatching {
            withContext(Dispatchers.IO) {
                TTSResult(
                    content = appContext.assets.open("sample.wav"),
                    mimeType = SupportedAudioType.WAV,
                )
            }
        }
    }
}