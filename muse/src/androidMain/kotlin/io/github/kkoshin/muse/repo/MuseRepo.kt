package io.github.kkoshin.muse.repo

import android.content.Context
import com.github.foodiestudio.sugar.ExperimentalSugarApi
import com.github.foodiestudio.sugar.storage.AppFileHelper
import io.github.kkoshin.muse.database.AppDatabase
import io.github.kkoshin.muse.repo.model.Script
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

@OptIn(ExperimentalSugarApi::class)
actual class MuseRepo(
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

    actual fun getPcmCache(
        voiceId: String,
        phrase: String,
    ): File = getVoiceDir(voiceId).resolve("$phrase.pcm")

    actual suspend fun queryAllScripts(): List<Script> = withContext(Dispatchers.IO) {
        scriptDao.queryAllScripts().executeAsList().map {
            Script(UUID.fromString(it.id), it.title, it.text, it.created_At)
        }
    }

    actual suspend fun queryScript(id: UUID): Script? = withContext(Dispatchers.IO) {
        scriptDao.queryScirptById(id.toString()).executeAsOneOrNull()?.let {
            Script(UUID.fromString(it.id), it.title, it.text, it.created_At)
        }
    }

    actual suspend fun insertScript(script: Script) = withContext(Dispatchers.IO) {
        scriptDao.insertScript(script.id.toString(), script.title, script.text, script.createAt)
    }

    actual suspend fun deleteScript(id: UUID) =
        withContext(Dispatchers.IO) { scriptDao.deleteScriptById(id.toString()) }

    actual companion object {
        actual fun getExportRelativePath(appContext: Context): String {
            val resourceId =
                appContext.resources.getIdentifier("app_name", "string", appContext.packageName)
            return "Download/${appContext.getString(resourceId)}"
        }

        actual fun getMusicRelativePath(appContext: Context): String {
            val resourceId =
                appContext.resources.getIdentifier("app_name", "string", appContext.packageName)
            return "Music/${appContext.getString(resourceId)}"
        }
    }
}
