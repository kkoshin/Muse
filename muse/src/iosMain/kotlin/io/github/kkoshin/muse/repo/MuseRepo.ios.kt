@file:OptIn(ExperimentalUuidApi::class)

package io.github.kkoshin.muse.repo

import io.github.kkoshin.muse.repo.model.Script
import okio.Path
import okio.Path.Companion.toPath
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// TODO: Implement MuseRepo
actual class MuseRepo {
    actual fun getPcmCache(voiceId: String, phrase: String): Path {
        return "".toPath()
    }

    actual suspend fun queryAllScripts(): List<Script> {
        return emptyList()
    }

    actual suspend fun queryScript(id: Uuid): Script? = null

    actual suspend fun insertScript(script: Script) {
    }

    actual suspend fun deleteScript(id: Uuid) {
    }

    actual companion object {
        actual fun getExportRelativePath(): String = "TODO"

        actual fun getMusicRelativePath(): String = "TODO"
    }

}