package io.github.kkoshin.muse.audio

import okio.Path

interface AudioMetadataRetriever {
    suspend fun getAudioMetadata(path: Path): AudioMetadata?
}
