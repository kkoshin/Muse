package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * export interface RecordingResponse {
 *     recording_id: string;
 *     mime_type: string;
 *     size_bytes: number;
 *     upload_date_unix: number;
 *     transcription: string;
 * }
 */
@Serializable
class RecordingResponse(
    @SerialName("recording_id")
    val recordingId: String,
    @SerialName("mime_type")
    val mimeType: String,
    @SerialName("size_bytes")
    val sizeBytes: Long,
    @SerialName("upload_date_unix")
    val uploadDateUnix: Long,
    @SerialName("transcription")
    val transcription: String,
)
