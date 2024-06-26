package io.github.kkoshin.muse.tts

import io.github.kkoshin.muse.audio.AudioSampleMetadata
import kotlinx.serialization.Serializable
import java.io.InputStream

interface TTSProvider {
    /**
     * 生成对应的音频文件流，
     */
    suspend fun generate(text: String): Result<TTSResult>

    /**
     * 剩余的 quota, 单位: Character
     */
    suspend fun queryQuota(): Result<CharacterQuota>

    suspend fun queryVoices(): Result<List<Voice>>
}

enum class SupportedAudioType {
    MP3,
    PCM,
    WAV,
}

data class TTSResult(
    val content: InputStream,
    val mimeType: SupportedAudioType,
    val audioSampleMetadata: AudioSampleMetadata,
)

data class CharacterQuota(
    val consumed: Int,
    val total: Int,
) {
    val remaining: Int
        get() = total - consumed

    companion object {
        val unknown = CharacterQuota(-1, -1)
    }
}

@Serializable
data class Voice(
    val voiceId: String,
    val name: String,
    val description: String?,
    val previewUrl: String,
)