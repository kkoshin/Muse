package io.github.kkoshin.muse

import android.content.Context
import com.github.foodiestudio.sugar.ExperimentalSugarApi
import com.github.foodiestudio.sugar.storage.AppFileHelper
import java.io.File

@OptIn(ExperimentalSugarApi::class)
class MuseRepo(
    context: Context,
) {
    private val appFileHelper = AppFileHelper(context)

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

    companion object {
        fun getExportRelativePath(appContext: Context): String = "Download/${appContext.getString(R.string.app_name)}"
    }
}