package io.github.kkoshin.muse.audio

import okio.IOException
import okio.Path

expect fun writeWaveHeader(filePath: Path, audioMetadata: AudioSampleMetadata)

internal class WaveHeaderWriter(
    private val filePath: Path,
    private val audioMetadata: AudioSampleMetadata,
) {
    @Throws(IOException::class)
    fun writeHeader() {
        writeWaveHeader(filePath, audioMetadata)
    }
}