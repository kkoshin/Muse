package io.github.kkoshin.muse.repo

import android.content.Context
import io.github.kkoshin.muse.repo.model.Script
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

expect class MuseRepo {
    fun getPcmCache(
        voiceId: String,
        phrase: String,
    ): File

    suspend fun queryAllScripts(): List<Script>

    suspend fun queryScript(id: UUID): Script?

    suspend fun insertScript(script: Script)

    suspend fun deleteScript(id: UUID)

    companion object {
        fun getExportRelativePath(appContext: Context): String

        fun getMusicRelativePath(appContext: Context): String
    }
}

// TODO: 文本过大，加载会很慢，甚至导致 ANR，暂时限定文本长度
const val MAX_TEXT_LENGTH = 10_000

suspend fun MuseRepo.queryPhrases(scriptId: UUID): List<String>? =
    withContext(Dispatchers.Default) {
        queryScript(scriptId)?.text?.take(MAX_TEXT_LENGTH)
            ?.split(' ', '\n')?.filter { it.isNotBlank() }
    }
