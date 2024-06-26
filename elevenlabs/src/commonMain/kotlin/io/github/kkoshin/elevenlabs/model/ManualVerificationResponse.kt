package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * export interface ManualVerificationResponse {
 *     extra_text: string;
 *     request_time_unix: number;
 *     files: ElevenLabs.ManualVerificationFileResponse[];
 * }
 */

@Serializable
class ManualVerificationResponse(
    @SerialName("extra_text")
    val extraText: String,
    @SerialName("request_time_unix")
    val requestTimeUnix: Long,
    @SerialName("files")
    val files: List<ManualVerificationFileResponse>,
)
