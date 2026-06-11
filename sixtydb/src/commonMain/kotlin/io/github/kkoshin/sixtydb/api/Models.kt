package io.github.kkoshin.sixtydb.api

import io.github.kkoshin.sixtydb.SixtyDbClient
import io.github.kkoshin.sixtydb.model.ModelListResponse
import io.github.kkoshin.sixtydb.model.SixtyDbModel
import io.ktor.resources.Resource

@Resource("/tts/models")
class TtsModels

@Resource("/stt/models")
class SttModels

/** `GET /tts/models` — typically returns "60db Fast" and "60db Quality". */
suspend fun SixtyDbClient.getTtsModels(): Result<List<SixtyDbModel>> =
    get<TtsModels, ModelListResponse>(TtsModels()).map { it.data }

/** `GET /stt/models` — currently `60db-stt-v01`. */
suspend fun SixtyDbClient.getSttModels(): Result<List<SixtyDbModel>> =
    get<SttModels, ModelListResponse>(SttModels()).map { it.data }
