package io.github.kkoshin.sixtydb.api

import io.github.kkoshin.sixtydb.NdjsonAudioSource
import io.github.kkoshin.sixtydb.SixtyDbClient
import io.github.kkoshin.sixtydb.model.TtsSynthesizeRequest
import io.github.kkoshin.sixtydb.model.TtsSynthesizeResponse
import io.ktor.resources.Resource
import io.ktor.utils.io.ByteReadChannel
import okio.Buffer
import okio.Source
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Resource("/tts-synthesize")
class TtsSynthesize

@Resource("/tts-stream")
class TtsStream

/**
 * `POST /tts-stream` — returns each NDJSON line as decoded audio bytes via an
 * `okio.Source`. This is the recommended path for `generate()` — it gives a
 * lower time-to-first-byte than the sync endpoint and matches the streaming
 * shape of ElevenLabs' `textToSpeech` so the upstream pipeline is identical.
 */
suspend fun SixtyDbClient.textToSpeechStream(
    voiceId: String,
    text: String,
    outputFormat: String = "mp3",
    enhance: Boolean = true,
    speed: Double = 1.0,
    stability: Int = 50,
    similarity: Int = 75,
): Result<Source> {
    val body = TtsSynthesizeRequest(
        text = text,
        voiceId = voiceId,
        enhance = enhance,
        speed = speed,
        stability = stability,
        similarity = similarity,
        outputFormat = outputFormat,
    )
    return post<TtsSynthesizeRequest, TtsStream, ByteReadChannel>(
        TtsStream(),
        data = body,
    ).mapCatching {
        NdjsonAudioSource(it)
    }
}

/**
 * `POST /tts-synthesize` — one-shot synthesis. Returns the full audio as an
 * `okio.Source` backed by a `Buffer`. Use for short text or when you need the
 * total length up-front.
 */
@OptIn(ExperimentalEncodingApi::class)
suspend fun SixtyDbClient.textToSpeechSync(
    voiceId: String,
    text: String,
    outputFormat: String = "mp3",
    enhance: Boolean = true,
    speed: Double = 1.0,
    stability: Int = 50,
    similarity: Int = 75,
): Result<Source> {
    val body = TtsSynthesizeRequest(
        text = text,
        voiceId = voiceId,
        enhance = enhance,
        speed = speed,
        stability = stability,
        similarity = similarity,
        outputFormat = outputFormat,
    )
    return post<TtsSynthesizeRequest, TtsSynthesize, TtsSynthesizeResponse>(
        TtsSynthesize(),
        data = body,
    ).mapCatching {
        check(it.success && !it.audioBase64.isNullOrBlank()) {
            "60db /tts-synthesize returned no audio: ${it.message ?: "unknown"}"
        }
        Buffer().apply { write(Base64.decode(it.audioBase64)) }
    }
}
