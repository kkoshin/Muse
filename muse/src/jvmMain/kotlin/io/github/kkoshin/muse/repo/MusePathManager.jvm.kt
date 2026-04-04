package io.github.kkoshin.muse.repo

import okio.Path
import okio.Path.Companion.toPath
import io.github.kkoshin.muse.platformbridge.SystemFileSystem

actual class MusePathManager {
    actual fun getVoiceDir(voiceId: String): Path {
        val path = (System.getProperty("java.io.tmpdir").toPath() / "muse" / "voices" / voiceId)
        if (!SystemFileSystem.exists(path)) {
            SystemFileSystem.createDirectories(path)
        }
        return path
    }

    actual companion object {
        actual fun getExportRelativePath(): String = "export"
        actual fun getMusicRelativePath(): String = "music"
    }
}
