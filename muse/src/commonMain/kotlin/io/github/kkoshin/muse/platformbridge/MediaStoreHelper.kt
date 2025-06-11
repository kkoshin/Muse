package io.github.kkoshin.muse.platformbridge

import okio.BufferedSink
import okio.Path

expect class MediaStoreHelper {
    fun <T> saveAudio(
        relativePath: String,
        fileName: String,
        action: BufferedSink.() -> T
    ): Path

    /**
     * 在公共下载文件夹下的 [relativePath] 下创建 [fileName] 文件
     * @return 这个文件的写入流
     */
    fun exportFileToDownload(
        fileName: String,
        relativePath: String? = null
    ): Path
}