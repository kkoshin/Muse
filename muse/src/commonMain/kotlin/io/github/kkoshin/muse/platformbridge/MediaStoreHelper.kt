package io.github.kkoshin.muse.platformbridge

import okio.BufferedSink
import okio.Path

expect class MediaStoreHelper {
    fun <T> saveAudio(
        relativePath: String,
        fileName: String,
        action: BufferedSink.() -> T
    ): Path
}