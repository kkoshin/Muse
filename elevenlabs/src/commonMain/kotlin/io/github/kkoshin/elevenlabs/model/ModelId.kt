package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ModelId {
    // multiple language support
    @SerialName("eleven_multilingual_v2")
    MultilingualV2,

    // multiple language support
    @SerialName("eleven_multilingual_sts_v2")
    MultilingualStsV2,

    // multiple language support
    @SerialName("eleven_multilingual_v1")
    Multilingual,

    // english only with 10000 free quota
    @SerialName("eleven_monolingual_v1")
    EnglishMono,

    // english only with 5000 free quota
    @SerialName("eleven_english_sts_v2")
    EnglishSts,

    // english only with 30000 free quota
    @SerialName("eleven_turbo_v2")
    EnglishTurbo,
}