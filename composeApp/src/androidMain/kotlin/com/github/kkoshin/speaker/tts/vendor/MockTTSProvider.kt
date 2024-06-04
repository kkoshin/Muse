package com.github.kkoshin.speaker.tts.vendor

import com.github.kkoshin.speaker.tts.TTSProvider
import com.github.kkoshin.speaker.tts.TTSResult

class MockTTSProvider : TTSProvider {
    override suspend fun generate(text: String): Result<TTSResult> {
        TODO("Not yet implemented")
    }
}