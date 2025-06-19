package io.github.kkoshin.muse.repo

import okio.Path

expect class MusePathManager {
    // make sure the directory exists
    fun getVoiceDir(voiceId: String): Path

    companion object {
        fun getExportRelativePath(): String

        fun getMusicRelativePath(): String
    }
}