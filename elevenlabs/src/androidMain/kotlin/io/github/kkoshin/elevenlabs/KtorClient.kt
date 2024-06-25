package io.github.kkoshin.elevenlabs

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

private const val BASE_HOST = "api.elevenlabs.io/v1"

actual val ktorClient = HttpClient(OkHttp) {
    install(Logging) {
        level = LogLevel.ALL
        logger = object : Logger {
            override fun log(message: String) {
                Log.d("Network", message)
            }
        }
    }
    install(ContentNegotiation) {
        json(
            Json {
                @OptIn(ExperimentalSerializationApi::class)
                explicitNulls = false
            },
        )
    }
    install(Resources)
    defaultRequest {
        contentType(ContentType.Application.Json)
        url {
            protocol = URLProtocol.HTTPS
            host = BASE_HOST
        }
    }
}