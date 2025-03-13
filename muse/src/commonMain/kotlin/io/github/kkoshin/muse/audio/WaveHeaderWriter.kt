package io.github.kkoshin.muse.audio

import okio.IOException
import okio.Path
import okio.Path.Companion.toPath

expect fun writeWaveHeader(filePath: Path, audioMetadata: AudioSampleMetadata)

internal class WaveHeaderWriter(
    private val filePath: String,
    private val audioMetadata: AudioSampleMetadata,
) {
    @Throws(IOException::class)
    fun writeHeader() {
        writeWaveHeader(filePath.toPath(), audioMetadata)
    }
}