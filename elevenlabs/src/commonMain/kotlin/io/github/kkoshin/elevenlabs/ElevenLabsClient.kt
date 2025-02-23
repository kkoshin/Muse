package io.github.kkoshin.elevenlabs

import io.github.kkoshin.elevenlabs.error.ElevenLabsError
import io.github.kkoshin.elevenlabs.error.UnprocessableEntityError
import io.github.kkoshin.elevenlabs.model.APIError
import io.github.kkoshin.elevenlabs.model.HttpValidationError
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.headers
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

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

    internal suspend inline fun <reified R : Any, reified W> postAsForm(
        resources: R,
        audioFile: ByteArray, // 修改为接收字节数组
        fileName: String,
        contentType: ContentType
    ): Result<W> = runCatching {
        ktorClient.post<R>(resources) {
            headers {
                append("xi-api-key", apiKey)
            }
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            "audio",
                            audioFile,
                            headers = Headers.build {
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "form-data; name=\"audio\"; filename=\"$fileName\""
                                )
                                append(HttpHeaders.ContentType, contentType.toString())
                            }
                        )
                    })
            )
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
