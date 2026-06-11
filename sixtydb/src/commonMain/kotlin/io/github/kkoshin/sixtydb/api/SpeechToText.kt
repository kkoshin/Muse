package io.github.kkoshin.sixtydb.api

import io.github.kkoshin.sixtydb.SixtyDbClient
import io.github.kkoshin.sixtydb.model.SttResponse
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.resources.Resource
import okio.Source
import okio.buffer

@Resource("/stt")
class Stt

/**
 * `POST /stt` — multipart batch transcription. Mirrors ElevenLabs'
 * `transcribeWithAudio` so the upstream `STTProvider` adapter can call either.
 */
suspend fun SixtyDbClient.transcribeAudio(
    source: Source,
    fileName: String,
    language: String? = null,
    diarize: Boolean? = null,
    returnTimestamps: String? = null,
): Result<SttResponse> = postForm(Stt()) {
    append(
        "file",
        source.buffer().readByteArray(),
        headers = Headers.build {
            append(HttpHeaders.ContentType, ContentType.Audio.Any.toString())
            append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
        },
    )
    language?.let { append("language", it) }
    diarize?.let { append("diarize", it.toString()) }
    returnTimestamps?.let { append("return_timestamps", it) }
}
