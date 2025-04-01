package io.github.kkoshin.elevenlabs

import io.ktor.client.HttpClient

internal const val BASE_URL = "https://api.elevenlabs.io/v1/"

expect val ktorClient: HttpClient