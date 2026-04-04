package io.github.kkoshin.elevenlabs

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

actual val ktorClient: HttpClient = HttpClient(OkHttp)
