package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SoundGenerationRequest(
    val text: String,
    // [0.5, 22.0], if not specified, will guess based on text length
    @SerialName("duration_seconds")
    val durationSeconds: Double? = null,

    // [0.0, 1.0]
    @SerialName("prompt_influence")
    val promptInfluence: Double = 0.3,
) {
    init {
        require(durationSeconds == null || durationSeconds in 0.5..22.0) { "duration_seconds must be in [0.5, 22.0]" }
        require(promptInfluence in 0.0..1.0) { "prompt_influence must be in [0.0, 1.0]" }
    }
}