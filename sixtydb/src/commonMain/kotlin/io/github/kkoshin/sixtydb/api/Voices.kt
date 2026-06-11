package io.github.kkoshin.sixtydb.api

import io.github.kkoshin.sixtydb.SixtyDbClient
import io.github.kkoshin.sixtydb.model.SixtyDbVoice
import io.github.kkoshin.sixtydb.model.VoiceListResponse
import io.ktor.resources.Resource

@Resource("/default-voices")
class DefaultVoices

@Resource("/my-voices")
class MyVoices

/** `GET /default-voices` — the public 60db voice library. */
suspend fun SixtyDbClient.getDefaultVoices(): Result<List<SixtyDbVoice>> =
    get<DefaultVoices, VoiceListResponse>(DefaultVoices()).map { it.data }

/** `GET /my-voices` — voices owned by the caller's account. */
suspend fun SixtyDbClient.getMyVoices(): Result<List<SixtyDbVoice>> =
    get<MyVoices, VoiceListResponse>(MyVoices()).map { it.data }
