package io.github.kkoshin.elevenlabs.api

import io.github.kkoshin.elevenlabs.ElevenLabsClient
import io.github.kkoshin.elevenlabs.model.SpeechToTextChunkResponseModel
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.resources.Resource
import okio.Source
import okio.buffer

@Resource("/speech-to-text")
class SpeechToText

private suspend fun ElevenLabsClient.transcribe(
    source: Source,
    fileName: String,
    mimeType: String,
    modelId: String = "scribe_v1",
): Result<SpeechToTextChunkResponseModel> = postForm(SpeechToText()) {
    append("model_id", modelId)
    append(
        "file",
        source.buffer().readByteArray(),
        headers = Headers.build {
            append(HttpHeaders.ContentType, mimeType)
            append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
        }
    )
}

suspend fun ElevenLabsClient.transcribeWithVideo(
    source: Source,
    fileName: String,
    modelId: String = "scribe_v1",
): Result<SpeechToTextChunkResponseModel> =
    transcribe(source, fileName, ContentType.Video.Any.toString(), modelId)

suspend fun ElevenLabsClient.transcribeWithAudio(
    source: Source,
    fileName: String,
    modelId: String = "scribe_v1",
): Result<SpeechToTextChunkResponseModel> =
    transcribe(source, fileName, ContentType.Audio.Any.toString(), modelId)