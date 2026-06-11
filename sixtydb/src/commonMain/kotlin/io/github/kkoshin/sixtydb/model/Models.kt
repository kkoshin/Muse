package io.github.kkoshin.sixtydb.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// =====================================================================
// 60db request/response models — fields named per https://docs.60db.ai
// =====================================================================

/** Error envelope returned for any non-2xx response. */
@Serializable
data class SixtyDbErrorEnvelope(
    val detail: String? = null,
    val code: String? = null,
)

// ---- TTS ----------------------------------------------------------------

/** Output container for TTS. 60db documents mp3 / wav / ogg / flac. */
@Serializable
enum class SixtyDbOutputFormat(val raw: String) {
    @SerialName("mp3") Mp3("mp3"),
    @SerialName("wav") Wav("wav"),
    @SerialName("ogg") Ogg("ogg"),
    @SerialName("flac") Flac("flac"),
}

/** Body for `POST /tts-synthesize` and `POST /tts-stream`. */
@Serializable
data class TtsSynthesizeRequest(
    val text: String,
    @SerialName("voice_id") val voiceId: String,
    val enhance: Boolean = true,
    val speed: Double = 1.0,
    val stability: Int = 50,
    val similarity: Int = 75,
    @SerialName("output_format") val outputFormat: String = "mp3",
)

/** Response envelope for the sync `POST /tts-synthesize` endpoint. */
@Serializable
data class TtsSynthesizeResponse(
    val success: Boolean = false,
    @SerialName("audio_base64") val audioBase64: String? = null,
    @SerialName("sample_rate") val sampleRate: Int? = null,
    @SerialName("duration_seconds") val durationSeconds: Double? = null,
    @SerialName("output_format") val outputFormat: String? = null,
    val message: String? = null,
)

/** One NDJSON frame from `POST /tts-stream`. */
@Serializable
data class TtsStreamChunk(
    val type: String? = null,
    @SerialName("audioContent") val audioContent: String? = null,
    val message: String? = null,
)

// ---- STT ----------------------------------------------------------------

/** Response from `POST /stt`. */
@Serializable
data class SttResponse(
    @SerialName("request_id") val requestId: String? = null,
    val text: String = "",
    val language: String? = null,
    @SerialName("language_name") val languageName: String? = null,
    @SerialName("duration_sec") val durationSec: Double? = null,
)

// ---- Voices + Models ----------------------------------------------------

/** Voice descriptor used by `/default-voices` and `/my-voices`. */
@Serializable
data class SixtyDbVoice(
    @SerialName("voice_id") val voiceId: String,
    val name: String? = null,
    val description: String? = null,
    val gender: String? = null,
    val language: String? = null,
    @SerialName("preview_url") val previewUrl: String? = null,
)

/** Top-level body of the voice list endpoints. */
@Serializable
data class VoiceListResponse(
    val data: List<SixtyDbVoice> = emptyList(),
)

/** Catalog row from `/tts/models` and `/stt/models`. */
@Serializable
data class SixtyDbModel(
    val id: String,
    val name: String? = null,
    val description: String? = null,
)

@Serializable
data class ModelListResponse(
    val data: List<SixtyDbModel> = emptyList(),
)

// ---- Chat completions (OpenAI-compatible) -------------------------------

@Serializable
data class ChatMessage(
    val role: String,
    val content: String,
)

/** Request body for `POST /v1/chat/completions`. */
@Serializable
data class ChatCompletionRequest(
    val model: String = "60db-tiny",
    val messages: List<ChatMessage>,
    val stream: Boolean = false,
    val temperature: Double = 0.7,
    @SerialName("top_k") val topK: Int = 20,
    @SerialName("max_tokens") val maxTokens: Int? = null,
)

@Serializable
data class ChatCompletionChoice(
    val index: Int = 0,
    val message: ChatMessage? = null,
    @SerialName("finish_reason") val finishReason: String? = null,
)

@Serializable
data class ChatCompletionResponse(
    val choices: List<ChatCompletionChoice> = emptyList(),
)
