package io.github.kkoshin.elevenlabs

import io.ktor.client.HttpClient

internal const val BASE_HOST = "api.elevenlabs.io/v1"

expect val ktorClient: HttpClient