package io.github.kkoshin.sixtydb

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

actual val ktorClient: HttpClient = HttpClient(OkHttp) {
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
