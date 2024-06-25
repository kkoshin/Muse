package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * export interface PronunciationDictionaryVersionLocator {
 *     pronunciation_dictionary_id: string;
 *     version_id: string;
 * }
 */
@Serializable
data class PronunciationDictionaryVersionLocator(
    @SerialName(value = "pronunciation_dictionary_id")
    val pronunciationDictionaryId: String,
    @SerialName(value = "version_id")
    val versionId: String,
)
