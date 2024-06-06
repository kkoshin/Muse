package com.github.kkoshin.muse.tts.vendor

import com.github.kkoshin.muse.tts.TTSProvider
import com.github.kkoshin.muse.tts.TTSResult

class MockTTSProvider : TTSProvider {
    override suspend fun generate(text: String): Result<TTSResult> {
        TODO("Not yet implemented")
    }
}