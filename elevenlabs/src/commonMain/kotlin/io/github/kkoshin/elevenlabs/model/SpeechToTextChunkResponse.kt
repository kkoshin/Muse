package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Chunk-level detail of the transcription with timing information.
 */
@Serializable
data class SpeechToTextChunkResponseModel(
    /** The detected language code (e.g. 'eng' for English). */
    @SerialName("language_code")
    val languageCode: String,
    /** The confidence score of the language detection (0 to 1). */
    @SerialName("language_probability")
    val languageProbability: Double,
    /** The raw text of the transcription. */
    val text: String,
    /** List of words with their timing information. */
    val words: List<SpeechToTextWordResponseModel>
)

@Serializable
data class SpeechToTextWordResponseModel(
    /** The word or sound that was transcribed. */
    val text: String,
    /** The start time of the word or sound in seconds. */
    val start: Double? = null,
    /** The end time of the word or sound in seconds. */
    val end: Double? = null,
    /** The type of the word or sound. 'audio_event' is used for non-word sounds like laughter or footsteps. */
//    @Serializable(with = SpeechToTextWordResponseModelTypeSerializer::class)
    val type: SpeechToTextWordResponseModelType,
    /** Unique identifier for the speaker of this word. */
    @SerialName("speaker_id")
    val speakerId: String? = null,
    /** The characters that make up the word and their timing information. */
    val characters: List<SpeechToTextCharacterResponseModel>? = null
)

@Serializable
enum class SpeechToTextWordResponseModelType {
    @SerialName("word")
    WORD,

    @SerialName("spacing")
    SPACING,

    @SerialName("audio_event")
    AUDIO_EVENT;
}

//// 自定义序列化器用于处理与外部JSON格式的兼容性
//object SpeechToTextWordResponseModelTypeSerializer :
//    KSerializer<SpeechToTextWordResponseModelType> {
//    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SpeechToTextWordResponseModelType", PrimitiveKind.STRING)
//
//    override fun serialize(encoder: Encoder, value: SpeechToTextWordResponseModelType) {
//        val stringValue = when(value) {
//            SpeechToTextWordResponseModelType.WORD -> "word"
//            SpeechToTextWordResponseModelType.SPACING -> "spacing"
//            SpeechToTextWordResponseModelType.AUDIO_EVENT -> "audio_event"
//        }
//        encoder.encodeString(stringValue)
//    }
//
//    override fun deserialize(decoder: Decoder): SpeechToTextWordResponseModelType {
//        return when(val value = decoder.decodeString()) {
//            "word" -> SpeechToTextWordResponseModelType.WORD
//            "spacing" -> SpeechToTextWordResponseModelType.SPACING
//            "audio_event" -> SpeechToTextWordResponseModelType.AUDIO_EVENT
//            else -> throw SerializationException("Unknown SpeechToTextWordResponseModelType: $value")
//        }
//    }
//}

@Serializable
data class SpeechToTextCharacterResponseModel(
    /** The character that was transcribed. */
    val text: String,
    /** The start time of the character in seconds. */
    val start: Double,
    /** The end time of the character in seconds. */
    val end: Double
)