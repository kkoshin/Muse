package io.github.kkoshin.elevenlabs

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import platform.Foundation.NSLog

actual val ktorClient: HttpClient
    get() = HttpClient(Darwin) {
        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    NSLog("Network/Ktor: $message")
                }
            }
        }
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    @OptIn(ExperimentalSerializationApi::class)
                    explicitNulls = false
                },
            )
        }
        install(Resources)
        defaultRequest {
            contentType(ContentType.Application.Json)
            url(BASE_URL)
        }
    }