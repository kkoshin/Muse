package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * export interface VoiceSettings {
 *     stability?: number;
 *     similarity_boost?: number;
 *     style?: number;
 *     use_speaker_boost?: boolean;
 * }
 */
@Serializable
data class VoiceSettings(
    @SerialName("stability")
    val stability: Double? = null,
    @SerialName("similarity_boost")
    val similarityBoost: Double? = null,
    val style: Int? = null,
    @SerialName("use_speaker_boost")
    val useSpeakerBoost: Boolean? = null,
)
