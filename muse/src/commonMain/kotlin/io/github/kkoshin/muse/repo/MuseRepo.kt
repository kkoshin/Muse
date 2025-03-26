
package io.github.kkoshin.muse.repo

import io.github.kkoshin.muse.database.AppDatabase
import io.github.kkoshin.muse.repo.model.Script
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import okio.Path
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalUuidApi::class)
class MuseRepo(
    database: AppDatabase,
    private val pathManager: MusePathManager,
) {
    private val scriptDao = database.scriptQueries

    fun getPcmCache(
        voiceId: String,
        phrase: String,
    ): Path = pathManager.getVoiceDir(voiceId).resolve("$phrase.pcm")

    suspend fun queryAllScripts(): List<Script> = withContext(Dispatchers.IO) {
        scriptDao.queryAllScripts().executeAsList().map {
            Script(Uuid.parse(it.id), it.title, it.text, it.created_At)
        }
    }

    suspend fun queryScript(id: Uuid): Script? = withContext(Dispatchers.IO) {
        scriptDao.queryScirptById(id.toString()).executeAsOneOrNull()?.let {
            Script(Uuid.parse(it.id), it.title, it.text, it.created_At)
        }
    }

    suspend fun insertScript(script: Script) = withContext(Dispatchers.IO) {
        scriptDao.insertScript(script.id.toString(), script.title, script.text, script.createAt)
    }

    suspend fun deleteScript(id: Uuid) =
        withContext(Dispatchers.IO) { scriptDao.deleteScriptById(id.toString()) }
}

// TODO: 文本过大，加载会很慢，甚至导致 ANR，暂时限定文本长度
const val MAX_TEXT_LENGTH = 10_000

@OptIn(ExperimentalUuidApi::class)
suspend fun MuseRepo.queryPhrases(scriptId: Uuid): List<String>? =
    withContext(Dispatchers.Default) {
        queryScript(scriptId)?.text?.take(MAX_TEXT_LENGTH)
            ?.split(' ', '\n')?.filter { it.isNotBlank() }
    }
