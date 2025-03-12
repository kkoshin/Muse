package io.github.kkoshin.muse.core.provider

import okio.Source

interface AudioIsolationProvider {
    suspend fun removeBackgroundNoise(audio: Source, audioName: String): Result<ByteArray>
}