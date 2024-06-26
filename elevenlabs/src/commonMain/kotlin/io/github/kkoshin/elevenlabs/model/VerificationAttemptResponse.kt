package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * export interface VerificationAttemptResponse {
 *     text: string;
 *     date_unix: number;
 *     accepted: boolean;
 *     similarity: number;
 *     levenshtein_distance: number;
 *     recording?: ElevenLabs.RecordingResponse;
 * }
 */
@Serializable
class VerificationAttemptResponse(
    @SerialName("text")
    val text: String,
    @SerialName("date_unix")
    val dateUnix: Long,
    @SerialName("accepted")
    val accepted: Boolean,
    @SerialName("similarity")
    val similarity: Double,
    @SerialName("levenshtein_distance")
    val levenshteinDistance: Int,
    @SerialName("recording")
    val recording: RecordingResponse? = null,
)