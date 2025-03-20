@file:OptIn(ExperimentalUuidApi::class)

package io.github.kkoshin.muse.repo

import io.github.kkoshin.muse.repo.model.Script
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.Path
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

expect class MuseRepo {
    fun getPcmCache(
        voiceId: String,
        phrase: String,
    ): Path

    suspend fun queryAllScripts(): List<Script>

    suspend fun queryScript(id: Uuid): Script?

    suspend fun insertScript(script: Script)

    suspend fun deleteScript(id: Uuid)

    companion object {
        fun getExportRelativePath(): String

        fun getMusicRelativePath(): String
    }
}

// TODO: 文本过大，加载会很慢，甚至导致 ANR，暂时限定文本长度
const val MAX_TEXT_LENGTH = 10_000

suspend fun MuseRepo.queryPhrases(scriptId: Uuid): List<String>? =
    withContext(Dispatchers.Default) {
        queryScript(scriptId)?.text?.take(MAX_TEXT_LENGTH)
            ?.split(' ', '\n')?.filter { it.isNotBlank() }
    }
