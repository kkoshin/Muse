package io.github.kkoshin.muse.repo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.content.Context
import com.github.foodiestudio.sugar.ExperimentalSugarApi
import com.github.foodiestudio.sugar.storage.AppFileHelper
import io.github.kkoshin.muse.R
import io.github.kkoshin.muse.dashboard.Script
import io.github.kkoshin.muse.database.AppDatabase
import okio.buffer
import okio.source
import okio.use
import java.io.File
import java.util.UUID

@OptIn(ExperimentalSugarApi::class)
class MuseRepo(
    context: Context,
) {
    private val appFileHelper = AppFileHelper(context)

    private val database = AppDatabase(DriverFactory(context).createDriver())
    private val scriptDao = database.scriptQueries

    private val voicesDir: File by lazy {
        appFileHelper.requireCacheDir(false).resolve("voices")
    }

    private fun getVoiceDir(voiceId: String): File =
        voicesDir
            .resolve(voiceId)
            .also {
                it.mkdirs()
            }

    fun getPcmCache(
        voiceId: String,
        phrase: String,
    ): File = getVoiceDir(voiceId).resolve("$phrase.pcm")

    suspend fun queryAllScripts(): List<Script> = withContext(Dispatchers.IO) {
        scriptDao.queryAllScripts().executeAsList().map {
            Script(UUID.fromString(it.id), it.title, it.text, it.created_At)
        }
    }

    suspend fun queryScript(id: UUID): Script? = withContext(Dispatchers.IO) {
        scriptDao.queryScirptById(id.toString()).executeAsOneOrNull()?.let {
            Script(UUID.fromString(it.id), it.title, it.text, it.created_At)
        }
    }

    suspend fun insertScript(script: Script) = withContext(Dispatchers.IO) {
        scriptDao.insertScript(script.id.toString(), script.title, script.text, script.createAt)
    }

    suspend fun deleteScript(id: UUID) =
        withContext(Dispatchers.IO) { scriptDao.deleteScriptById(id.toString()) }

    companion object {
        fun getExportRelativePath(appContext: Context): String =
            "Download/${appContext.getString(R.string.app_name)}"
    }
}

// TODO: 文本过大，加载会很慢，甚至导致 ANR，暂时限定文本长度
const val MAX_TEXT_LENGTH = 10_000

suspend fun MuseRepo.queryPhrases(scriptId: UUID): List<String>? =
    withContext(Dispatchers.Default) {
        queryScript(scriptId)?.text?.take(MAX_TEXT_LENGTH)
            ?.split(' ', '\n')?.filter { it.isNotBlank() }
    }