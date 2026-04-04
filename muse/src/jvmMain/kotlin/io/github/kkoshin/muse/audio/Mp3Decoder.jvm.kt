package io.github.kkoshin.muse.audio

import okio.BufferedSink
import okio.Path

actual class Mp3Decoder {
    actual suspend fun decodeMp3ToPCM(
        pcmSink: BufferedSink,
        mp3Path: Path,
        volumeBoost: Float,
    ) {
        // No-op
    }
}
