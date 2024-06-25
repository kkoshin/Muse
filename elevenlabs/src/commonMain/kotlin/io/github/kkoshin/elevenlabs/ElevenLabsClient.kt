package io.github.kkoshin.elevenlabs

import io.github.kkoshin.elevenlabs.error.ElevenLabsError
import io.github.kkoshin.elevenlabs.error.UnprocessableEntityError
import io.github.kkoshin.elevenlabs.model.APIError
import io.github.kkoshin.elevenlabs.model.HttpValidationError
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.headers
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse

class ElevenLabsClient(
    private val apiKey: String,
) {
    internal suspend inline fun <reified T : Any> get(resource: T): HttpResponse =
        ktorClient.get(resource = resource) {
            headers {
                append("xi-api-key", apiKey)
            }
        }

    internal suspend inline fun <reified T, reified R : Any> post(
        resources: R,
        data: T,
    ): HttpResponse =
        ktorClient.post<R>(resources) {
            headers {
                append("xi-api-key", apiKey)
            }
            setBody(data)
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

internal suspend inline fun <reified T> HttpResponse.bodyAsResult(): Result<T> =
    runCatching {
        when (status.value) {
            200 -> body()
            422 -> body<HttpValidationError>().let {
                throw UnprocessableEntityError(it)
            }

            else -> body<APIError>().let {
                throw ElevenLabsError(status.value, it.detail.message)
            }
        }
    }
