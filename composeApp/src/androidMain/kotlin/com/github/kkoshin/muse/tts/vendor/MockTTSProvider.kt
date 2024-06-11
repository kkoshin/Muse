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
        return runCatching {
            withContext(Dispatchers.IO) {
                delay(1000)
                TTSResult(
                    content = appContext.assets.open("english.mp3"),
                    mimeType = SupportedAudioType.MP3,
                )
            }
        }
    }
}