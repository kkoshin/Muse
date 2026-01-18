package io.github.kkoshin.muse.platformbridge

import android.content.Context
import com.github.foodiestudio.sugar.ExperimentalSugarApi
import com.github.foodiestudio.sugar.storage.filesystem.media.MediaFile
import com.github.foodiestudio.sugar.storage.filesystem.media.MediaStoreType
import com.github.foodiestudio.sugar.storage.filesystem.toOkioPath
import okio.BufferedSink
import okio.Path

actual class MediaStoreHelper(val appContext: Context) {
    @OptIn(ExperimentalSugarApi::class)
    actual fun <T> saveAudio(
        relativePath: String,
        fileName: String,
        action: BufferedSink.() -> T
    ): Path {
        return MediaFile
            .create(
                appContext,
                MediaStoreType.Audio,
                fileName,
                relativePath,
                enablePending = true,
            ).let {
                it.write {
                    action()
                }
                it.releasePendingStatus()
                it.mediaUri.toOkioPath()
            }
    }

    @OptIn(ExperimentalSugarApi::class)
    actual fun exportFileToDownload(fileName: String, relativePath: String?): Path {
        val targetUri = MediaFile
            .create(
                appContext,
                MediaStoreType.Downloads,
                fileName,
                relativePath,
                enablePending = false,
            ).mediaUri
        return targetUri.toOkioPath()
    }
}