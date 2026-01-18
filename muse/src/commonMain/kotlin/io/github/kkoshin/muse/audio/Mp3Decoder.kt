package io.github.kkoshin.muse.audio

import okio.BufferedSink
import okio.Path

expect class Mp3Decoder() {

    suspend fun decodeMp3ToPCM(
        pcmSink: BufferedSink,
        mp3Path: Path,
        volumeBoost: Float = 1.0f,
    )
}