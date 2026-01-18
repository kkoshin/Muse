package io.github.kkoshin.muse.repo

import android.content.Context
import com.github.foodiestudio.sugar.ExperimentalSugarApi
import com.github.foodiestudio.sugar.storage.AppFileHelper
import okio.Path
import okio.Path.Companion.toOkioPath
import org.koin.java.KoinJavaComponent
import java.io.File

@OptIn(ExperimentalSugarApi::class)
actual class MusePathManager(context: Context) {

    @OptIn(ExperimentalSugarApi::class)
    private val appFileHelper = AppFileHelper(context)

    private val voicesDir: File by lazy {
        appFileHelper.requireCacheDir(false).resolve("voices")
    }

    actual fun getVoiceDir(voiceId: String): Path {
        return voicesDir
            .resolve(voiceId)
            .let {
                it.mkdirs()
                it.toOkioPath()
            }
    }

    actual companion object {
        private val appContext by KoinJavaComponent.inject<Context>(Context::class.java)

        actual fun getExportRelativePath(): String {
            val resourceId =
                appContext.resources.getIdentifier("app_name", "string", appContext.packageName)
            return "Download/${appContext.getString(resourceId)}"
        }

        actual fun getMusicRelativePath(): String {
            val resourceId =
                appContext.resources.getIdentifier("app_name", "string", appContext.packageName)
            return "Music/${appContext.getString(resourceId)}"
        }
    }
}