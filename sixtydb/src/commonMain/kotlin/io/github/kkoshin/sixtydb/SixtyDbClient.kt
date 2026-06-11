package io.github.kkoshin.sixtydb

import io.github.kkoshin.sixtydb.error.SixtyDbError
import io.github.kkoshin.sixtydb.model.SixtyDbErrorEnvelope
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.headers
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import kotlin.coroutines.cancellation.CancellationException

/**
 * 60db cloud API client — mirrors the shape of [io.github.kkoshin.elevenlabs.ElevenLabsClient]
 * so the rest of the codebase can swap providers behind the same processor
 * interfaces.
 *
 * Auth: every call sends `Authorization: Bearer ${apiKey}`. See
 *       https://docs.60db.ai
 *
 * Notes:
 * - Bearer auth (vs ElevenLabs' xi-api-key header) is the only major contract
 *   delta. The error envelope (`{ "detail": "..." }`) and JSON-only request
 *   body are otherwise compatible.
 * - Streaming endpoints (`/tts-stream`, `/v1/chat/completions?stream=true`)
 *   return raw `ByteReadChannel`s; the surrounding `api/*` extension
 *   functions handle decoding (NDJSON, SSE).
 */
class SixtyDbClient(
    private val apiKey: String,
) {
    internal suspend inline fun <reified T : Any, reified R> get(resource: T): Result<R> =
        runCatching {
            ktorClient.get(resource = resource) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $apiKey")
                }
            }
        }.mapCatching {
            it.bodyAsResult()
        }

    internal suspend inline fun <reified T, reified R : Any, reified W> post(
        resources: R,
        data: T,
    ): Result<W> =
        runCatching {
            ktorClient.post(resources) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $apiKey")
                }
                setBody(data)
            }
        }.mapCatching { it.bodyAsResult() }

    internal suspend inline fun <reified R : Any, reified W> postForm(
        resources: R,
        noinline formDataBuilder: FormBuilder.() -> Unit,
    ): Result<W> = runCatching {
        ktorClient.post(resources) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $apiKey")
            }
            setBody(
                MultiPartFormDataContent(
                    formData(formDataBuilder)
                )
            )
        }
    }.mapCatching {
        it.bodyAsResult()
    }

    companion object {
        /** Tear down the shared Ktor engine. Call on app exit. */
        fun destroyNetworkClient() {
            ktorClient.close()
        }
    }
}

@Throws(SixtyDbError::class, CancellationException::class)
internal suspend inline fun <reified T> HttpResponse.bodyAsResult(): T =
    when (status.value) {
        in 200..299 -> body()
        else -> {
            val envelope = runCatching { body<SixtyDbErrorEnvelope>() }.getOrNull()
            throw SixtyDbError(status.value, envelope?.detail ?: status.description)
        }
    }
