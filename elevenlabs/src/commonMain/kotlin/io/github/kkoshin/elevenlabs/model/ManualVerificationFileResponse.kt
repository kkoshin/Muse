package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * export interface ManualVerificationFileResponse {
 *     file_id: string;
 *     file_name: string;
 *     mime_type: string;
 *     size_bytes: number;
 *     upload_date_unix: number;
 * }
 */
@Serializable
class ManualVerificationFileResponse(
    @SerialName("file_id")
    val fileId: String,
    @SerialName("file_name")
    val fileName: String,
    @SerialName("mime_type")
    val mimeType: String,
    @SerialName("size_bytes")
    val sizeBytes: Long,
    @SerialName("upload_date_unix")
    val uploadDateUnix: Long,
)
