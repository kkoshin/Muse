package io.github.kkoshin.muse.audio

import kotlin.time.Duration
import okio.Path
import me.saket.bytesize.DecimalByteSize

data class AudioMetadata(
    val duration: Duration,
    val displayName: String,
    val size: DecimalByteSize
) {
    val points: Long
        get() {
            return (duration.inWholeSeconds * 1000) / 60
        }
}
