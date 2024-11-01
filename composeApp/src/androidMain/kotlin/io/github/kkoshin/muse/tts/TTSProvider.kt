package io.github.kkoshin.muse.tts

import io.github.kkoshin.elevenlabs.model.SubscriptionStatus
import io.github.kkoshin.muse.audio.AudioSampleMetadata
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.InputStream

interface TTSProvider {
    /**
     * 生成对应的音频文件流，
     */
    suspend fun generate(voiceId: String, text: String): Result<TTSResult>

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
    val status: SubscriptionStatus?,
) {
    val remaining: Int
        get() = total - consumed

    infix operator fun plus(other: CharacterQuota): CharacterQuota {
        require(consumed >= 0 && total >= 0)
        require(status == other.status)
        return CharacterQuota(consumed + other.consumed, total + other.total, status)
    }

    companion object {
        val unknown = CharacterQuota(-1, -1, null)
        val empty = CharacterQuota(0, 0, null)
    }
}

@Serializable
data class Voice(
    val voiceId: String,
    val name: String,
    val description: String?,
    val previewUrl: String,
    val accent: Accent,
    val useCase: String?,
    val gender: Gender?,
    val age: Age?,
) {
    @Serializable
    enum class Accent(
        val raw: String,
    ) {
        @SerialName("american")
        American("american"),

        @SerialName("british")
        British("british"),

        @SerialName("british-swedish")
        BritishSwedish("british-swedish"),

        @SerialName("australian")
        Australian("australian"),

        @SerialName("irish")
        Irish("irish"),

        @SerialName("transatlantic")
        Transatlantic("transatlantic"),

        @SerialName("other")
        Other("other"),
    }

    @Serializable
    enum class Gender(
        val raw: String,
    ) {
        @SerialName("male")
        Male("male"),

        @SerialName("female")
        Female("female"),
    }

    @Serializable
    enum class Age(
        val raw: String,
    ) {
        @SerialName("young")
        Young("young"),

        @SerialName("middle-aged")
        MiddleAged("middle-aged"),

        @SerialName("old")
        Old("old"),

        @SerialName("other")
        Other("other"),
    }
}