package io.github.kkoshin.muse.repo

import io.github.kkoshin.muse.platformbridge.logcat
import io.github.kkoshin.muse.platformbridge.toNsUrl
import io.github.kkoshin.muse.platformbridge.toOkioPath
import kotlinx.cinterop.BooleanVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import okio.Path
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual class MusePathManager {

    @OptIn(ExperimentalForeignApi::class)
    private val voicesDir: NSURL? by lazy {
        val cacheDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSCachesDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        cacheDirectory?.URLByAppendingPathComponent("voices", true)
    }

    actual fun getVoiceDir(voiceId: String): Path {
        return voicesDir!!.toOkioPath()!!.resolve(voiceId).also {
            ensureDirExists(it.toNsUrl()!!)
        }
    }

    /**
     * Ensures that the given directory exists.
     * if it doesn't exist, it will be created.
     *
     * @param path the directory to check
     * @return true if the directory exists, false otherwise
     */
    @OptIn(ExperimentalForeignApi::class)
    private fun ensureDirExists(url: NSURL): Boolean {
        memScoped {
            NSFileManager.defaultManager.let {
                val isDir = alloc<BooleanVar>()
                val exists = it.fileExistsAtPath(url.path!!, isDirectory = isDir.ptr)
                if (exists && isDir.value) {
                    return true
                }
                try {
                    it.createDirectoryAtURL(
                        url,
                        withIntermediateDirectories = true,
                        attributes = null,
                        error = null
                    )
                    return true
                } catch (e: Throwable) {
                    logcat { "Failed to create directory at $url: $e" }
                    return false
                }
            }
        }
    }

    actual companion object {
        actual fun getExportRelativePath(): String = "Exports"

        actual fun getMusicRelativePath(): String = "Music"
    }

}