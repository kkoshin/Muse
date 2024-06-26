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
        appFileHelper.requireCacheDir(false).resolve("voices").also {
            it.mkdirs()
        }
    }

    fun getPcmCache(phrase: String): File = voicesDir.resolve("$phrase.pcm")
}