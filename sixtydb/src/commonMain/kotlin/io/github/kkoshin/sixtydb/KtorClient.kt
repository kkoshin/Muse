package io.github.kkoshin.sixtydb

import io.ktor.client.HttpClient

internal const val BASE_URL = "https://api.60db.ai/"

expect val ktorClient: HttpClient
