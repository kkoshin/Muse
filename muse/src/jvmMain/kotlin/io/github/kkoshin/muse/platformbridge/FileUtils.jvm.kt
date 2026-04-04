package io.github.kkoshin.muse.platformbridge

import okio.Path
import okio.Sink
import okio.Path.Companion.toPath
import java.awt.Desktop

actual fun shareAudioFile(path: Path): Result<Unit> = Result.failure(Exception("Not implemented on Desktop"))

actual fun openFile(path: Path): Result<Unit> = runCatching {
    if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().open(path.toFile())
    } else {
        throw Exception("Desktop not supported")
    }
}

actual fun createCacheFile(fileName: String, sensitive: Boolean): Path {
    val tempDir = System.getProperty("java.io.tmpdir").toPath()
    return tempDir / fileName
}

actual fun Path.toSink(): Sink = SystemFileSystem.sink(this)
