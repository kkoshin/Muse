package io.github.kkoshin.muse.repo

import okio.Path

actual class MusePathManager {
    actual fun getVoiceDir(voiceId: String): Path {
        TODO("Not yet implemented")
    }

    actual companion object {
        actual fun getExportRelativePath(): String = "TODO"

        actual fun getMusicRelativePath(): String = "TODO"
    }

}