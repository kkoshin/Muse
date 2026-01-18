package io.github.kkoshin.muse.audio

import okio.BufferedSink

data class Mp3Metadata(
    val id3TagArtist: String,
    val id3TagYear: String,
)

expect class Mp3Encoder() {
    suspend fun encode(
        wavParser: WavParser,
        outputSink: BufferedSink,
        metadata: Mp3Metadata,
    )
}