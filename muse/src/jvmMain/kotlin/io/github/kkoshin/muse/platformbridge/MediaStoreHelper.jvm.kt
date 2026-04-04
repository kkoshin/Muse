package io.github.kkoshin.muse.platformbridge

import okio.BufferedSink
import okio.Path
import okio.Path.Companion.toPath

actual class MediaStoreHelper {
    actual fun <T> saveAudio(
        relativePath: String,
        fileName: String,
        action: BufferedSink.() -> T
    ): Path = "/tmp/$fileName".toPath()

    actual fun exportFileToDownload(
        fileName: String,
        relativePath: String?
    ): Path = "/tmp/$fileName".toPath()
}
