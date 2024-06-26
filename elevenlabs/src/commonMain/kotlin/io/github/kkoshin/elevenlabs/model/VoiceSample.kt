package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * export interface VoiceSample {
 *     sample_id?: string;
 *     file_name?: string;
 *     mime_type?: string;
 *     size_bytes?: number;
 *     hash?: string;
 * }
 */
@Serializable
data class VoiceSample(
    @SerialName("sample_id")
    val sampleId: String?,
    @SerialName("file_name")
    val fileName: String?,
    @SerialName("mime_type")
    val mimeType: String?,
    @SerialName("size_bytes")
    val sizeBytes: Long?,
    @SerialName("hash")
    val hash: String?,
)