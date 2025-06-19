package io.github.kkoshin.muse.platformbridge

import kotlinx.cinterop.ExperimentalForeignApi
import okio.BufferedSink
import okio.FileSystem
import okio.Path
import platform.Foundation.NSFileManager
import platform.Foundation.NSMusicDirectory
import platform.Foundation.NSUserDomainMask

actual class MediaStoreHelper {
    // TODO: 使用 MPMediaLibrary 保存到用户音乐库里
    @OptIn(ExperimentalForeignApi::class)
    actual fun <T> saveAudio(
        relativePath: String,
        fileName: String,
        action: BufferedSink.() -> T
    ): Path {
        check(relativePath.isNotEmpty() && fileName.isNotEmpty())
        val musicDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSMusicDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        val target = musicDirectory!!.URLByAppendingPathComponent(
            "$relativePath/$fileName",
            isDirectory = false
        )
        FileSystem.SYSTEM.write(target!!.toOkioPath()!!) {
            action()
        }
        return target.toOkioPath()!!
    }
}
