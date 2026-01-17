package io.github.kkoshin.muse.platformbridge

import kotlinx.cinterop.ExperimentalForeignApi
import okio.BufferedSink
import okio.FileSystem
import okio.Path
import platform.Foundation.NSFileManager
import platform.Foundation.NSMusicDirectory
import platform.Foundation.NSUserDomainMask
import platform.Foundation.URLByAppendingPathComponent

actual class MediaStoreHelper {
    // TODO: 使用 MPMediaLibrary 保存到用户音乐库里
    @OptIn(ExperimentalForeignApi::class)
    actual fun <T> saveAudio(
        relativePath: String,
        fileName: String,
        action: BufferedSink.() -> T
    ): Path {
        check(relativePath.isNotEmpty() && fileName.isNotEmpty())
        val fileManager = NSFileManager.defaultManager
        val musicDirectory = fileManager.URLForDirectory(
            directory = NSMusicDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null,
        )!!
        
        val targetDirectory = musicDirectory.URLByAppendingPathComponent(relativePath, isDirectory = true)!!
        
        if (!fileManager.fileExistsAtPath(targetDirectory.path!!)) {
            fileManager.createDirectoryAtURL(
                url = targetDirectory,
                withIntermediateDirectories = true,
                attributes = null,
                error = null
            )
        }
        
        val target = targetDirectory.URLByAppendingPathComponent(fileName, isDirectory = false)!!
        FileSystem.SYSTEM.write(target.toOkioPath()!!) {
            action()
        }
        return target.toOkioPath()!!
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun exportFileToDownload(fileName: String, relativePath: String?): Path {
        val fileManager = NSFileManager.defaultManager
        val documentsDirectory = fileManager.URLForDirectory(
            directory = platform.Foundation.NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null,
        )
        
        var targetDirectory = documentsDirectory!!
        if (relativePath != null) {
            targetDirectory = targetDirectory.URLByAppendingPathComponent(relativePath, isDirectory = true)!!
            if (!fileManager.fileExistsAtPath(targetDirectory.path!!)) {
                fileManager.createDirectoryAtURL(
                    url = targetDirectory,
                    withIntermediateDirectories = true,
                    attributes = null,
                    error = null
                )
            }
        }
        
        val targetFile = targetDirectory.URLByAppendingPathComponent(fileName, isDirectory = false)!!
        return targetFile.toOkioPath()!!
    }
}
