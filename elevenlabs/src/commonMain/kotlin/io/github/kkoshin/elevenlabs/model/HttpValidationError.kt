package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * export interface HttpValidationError {
 *     detail?: ElevenLabs.ValidationError[];
 * }
 */
@Serializable
data class HttpValidationError(
    @SerialName("detail")
    val detail: List<ValidationError>?,
)
