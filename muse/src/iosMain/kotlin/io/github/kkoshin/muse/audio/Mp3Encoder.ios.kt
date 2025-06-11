package io.github.kkoshin.muse.audio

import okio.BufferedSink

actual class Mp3Encoder {
    actual suspend fun encode(
        wavParser: WavParser,
        outputSink: BufferedSink,
        metadata: Mp3Metadata
    ) {
        // TODO: Implement Mp3Encoder
    }
}