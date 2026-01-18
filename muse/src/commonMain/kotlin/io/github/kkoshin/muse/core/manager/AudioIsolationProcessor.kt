package io.github.kkoshin.muse.core.manager

import okio.Path

interface AudioIsolationProcessor {
    suspend fun removeBackgroundNoiseAndSave(audioUri: Path): Result<Path>
}
