package io.github.kkoshin.elevenlabs

import io.github.kkoshin.elevenlabs.error.ElevenLabsError
import io.github.kkoshin.elevenlabs.error.UnprocessableEntityError
import io.github.kkoshin.elevenlabs.model.APIError
import io.github.kkoshin.elevenlabs.model.HttpValidationError
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.forms.formData
import io.ktor.client.request.headers
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ElevenLabsClient(
    private val apiKey: String,
) {
    internal suspend inline fun <reified T : Any, reified R> get(resource: T): Result<R> =
        runCatching {
            ktorClient.get(resource = resource) {
                headers {
                    append("xi-api-key", apiKey)
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
            ktorClient.post<R>(resources) {
                headers {
                    append("xi-api-key", apiKey)
                }
                setBody(data)
            }
        }.mapCatching { it.bodyAsResult() }

        // TODO: Add file upload
        internal suspend inline fun <reified T, reified R : Any, reified W> postAsForm(
            resources: R,
            fileName: String,
        ): Result<W> =  runCatching {
            ktorClient.post<R>(resources) {
                headers {
                    append("xi-api-key", apiKey)
                }
                contentType(ContentType.MultiPart.FormData)
                formData {
                    append("audio", fileName)
                }
            }
        }.mapCatching {
            it.bodyAsResult()
        }

    companion object {
        /**
         * 销毁 KtorClient，回收资源
         */
        fun destroyNetworkClient() {
            ktorClient.close()
        }
    }
}

@Throws(UnprocessableEntityError::class, ElevenLabsError::class)
internal suspend inline fun <reified T> HttpResponse.bodyAsResult(): T =
    when (status.value) {
        200 -> body()
        422 -> body<HttpValidationError>().let {
            throw UnprocessableEntityError(it)
        }

        else -> body<APIError>().let {
            throw ElevenLabsError(status.value, it.detail.message)
        }
    }
