package io.github.kkoshin.muse.repo

import com.benasher44.uuid.Uuid
import io.github.kkoshin.muse.repo.model.Script
import okio.Path

actual class MuseRepo {
    actual fun getPcmCache(voiceId: String, phrase: String): Path {
        TODO("Not yet implemented")
    }

    actual suspend fun queryAllScripts(): List<Script> {
        TODO("Not yet implemented")
    }

    actual suspend fun queryScript(id: Uuid): Script? {
        TODO("Not yet implemented")
    }

    actual suspend fun insertScript(script: Script) {
    }

    actual suspend fun deleteScript(id: Uuid) {
    }

    actual companion object {
        actual fun getExportRelativePath(): String {
            TODO("Not yet implemented")
        }

        actual fun getMusicRelativePath(): String {
            TODO("Not yet implemented")
        }

    }

}