package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * export interface TextToSpeechRequest {
 *     /**
 *      * When enable_logging is set to false full privacy mode will be used for the request. This will mean history features are unavailable for this request, including request stitching. Full privacy mode may only be used by enterprise customers.
 *      */
 *     enable_logging?: boolean;
 *     /**
 *      * You can turn on latency optimizations at some cost of quality. The best possible final latency varies by model.
 *      */
 *     optimize_streaming_latency?: ElevenLabs.OptimizeStreamingLatency;
 *     /**
 *      * The output format of the generated audio.
 *      */
 *     output_format?: ElevenLabs.OutputFormat;
 *     /** The text that will get converted into speech. */
 *     text: string;
 *     /** Identifier of the model that will be used, you can query them using GET /v1/models. The model needs to have support for text to speech, you can check this using the can_do_text_to_speech property. */
 *     model_id?: string;
 *     /** Voice settings overriding stored setttings for the given voice. They are applied only on the given request. */
 *     voice_settings?: ElevenLabs.VoiceSettings;
 *     /** A list of pronunciation dictionary locators (id, version_id) to be applied to the text. They will be applied in order. You may have up to 3 locators per request */
 *     pronunciation_dictionary_locators?: ElevenLabs.PronunciationDictionaryVersionLocator[];
 *     /** If specified, our system will make a best effort to sample deterministically, such that repeated requests with the same seed and parameters should return the same result. Determinism is not guaranteed. */
 *     seed?: number;
 *     /** The text that came before the text of the current request. Can be used to improve the flow of prosody when concatenating together multiple generations or to influence the prosody in the current generation. */
 *     previous_text?: string;
 *     /** The text that comes after the text of the current request. Can be used to improve the flow of prosody when concatenating together multiple generations or to influence the prosody in the current generation. */
 *     next_text?: string;
 *     /** A list of request_id of the samples that were generated before this generation. Can be used to improve the flow of prosody when splitting up a large task into multiple requests. The results will be best when the same model is used across the generations. In case both previous_text and previous_request_ids is send, previous_text will be ignored. A maximum of 3 request_ids can be send. */
 *     previous_request_ids?: string[];
 *     /** A list of request_id of the samples that were generated before this generation. Can be used to improve the flow of prosody when splitting up a large task into multiple requests. The results will be best when the same model is used across the generations. In case both next_text and next_request_ids is send, next_text will be ignored. A maximum of 3 request_ids can be send. */
 *     next_request_ids?: string[];
 * }
 */
@Serializable
data class TextToSpeechRequest(
    val text: String,
    @SerialName("model_id")
    val modelId: ModelId? = null,
    @SerialName("voice_settings")
    val voiceSettings: VoiceSettings? = null,
    @SerialName("pronunciation_dictionary_locators")
    val pronunciationDictionaryLocators: List<PronunciationDictionaryVersionLocator>? = null,
    val seed: Int? = null,
    @SerialName("previous_text")
    val previousText: String? = null,
    @SerialName("next_text")
    val nextText: String? = null,
    @SerialName("previous_request_ids")
    val previousRequestIds: List<String>? = null,
    @SerialName("next_request_ids")
    val nextRequestIds: List<String>? = null,
)
