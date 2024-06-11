package com.github.kkoshin.muse.tts

import java.io.InputStream

interface TTSProvider {
    /**
     * 生成对应的音频文件流，
     */
    suspend fun generate(text: String): Result<TTSResult>
}

enum class SupportedAudioType {
    MP3, PCM, WAV
}

data class TTSResult(
    val content: InputStream,
    val mimeType: SupportedAudioType,
)