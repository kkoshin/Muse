package io.github.kkoshin.muse.platformbridge

import kotlinx.cinterop.ExperimentalForeignApi
import okio.BufferedSink
import okio.FileSystem
import okio.Path
import platform.Foundation.NSFileManager
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
        val fileManager = NSFileManager.defaultManager
        val musicDirectory = fileManager.URLForDirectory(
            directory = platform.Foundation.NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null,
        )!!

        val targetDirectory = musicDirectory.URLByAppendingPathComponent(relativePath, isDirectory = true)!!
        val okioDirectoryPath = targetDirectory.toOkioPath()!!
        
        if (!FileSystem.SYSTEM.exists(okioDirectoryPath)) {
            logcat { "Creating directory: $okioDirectoryPath" }
            FileSystem.SYSTEM.createDirectories(okioDirectoryPath)
        }

        // Truncate filename if it's too long, but keep extension
        val extIndex = fileName.lastIndexOf('.')
        val ext = if (extIndex != -1) fileName.substring(extIndex) else ""
        val baseName = if (extIndex != -1) fileName.substring(0, extIndex) else fileName
        val sanitizedBaseName = baseName.replace("/", "_").replace(":", "_")
        val truncatedBaseName = if (sanitizedBaseName.length > 64) {
            sanitizedBaseName.substring(0, 60) + "_" + sanitizedBaseName.hashCode().toString(16)
        } else {
            sanitizedBaseName
        }
        val finalFileName = truncatedBaseName + ext
        
        val target = targetDirectory.URLByAppendingPathComponent(finalFileName, isDirectory = false)!!
        val okioPath = target.toOkioPath()!!
        logcat { "Saving audio to: $okioPath" }
        FileSystem.SYSTEM.write(okioPath) {
            action()
        }
        return okioPath
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
            val okioDirectoryPath = targetDirectory.toOkioPath()!!
            if (!FileSystem.SYSTEM.exists(okioDirectoryPath)) {
                logcat { "Creating export directory: $okioDirectoryPath" }
                FileSystem.SYSTEM.createDirectories(okioDirectoryPath)
            }
        }

        val extIndex = fileName.lastIndexOf('.')
        val ext = if (extIndex != -1) fileName.substring(extIndex) else ""
        val baseName = if (extIndex != -1) fileName.substring(0, extIndex) else fileName
        val sanitizedBaseName = baseName.replace("/", "_").replace(":", "_")
        val truncatedBaseName = if (sanitizedBaseName.length > 64) {
            sanitizedBaseName.substring(0, 60) + "_" + sanitizedBaseName.hashCode().toString(16)
        } else {
            sanitizedBaseName
        }
        val finalFileName = truncatedBaseName + ext

        val targetFile = targetDirectory.URLByAppendingPathComponent(finalFileName, isDirectory = false)!!
        val okioPath = targetFile.toOkioPath()!!
        logcat { "Exporting file to: $okioPath" }
        return okioPath
    }
}
