package io.github.kkoshin.muse.isolation

import okio.Source

interface AudioIsolationProvider {
    suspend fun removeBackgroundNoise(audio: Source, audioName: String): Result<ByteArray>
}