package io.github.kkoshin.muse.audio

// https://developer.mozilla.org/en-US/docs/Web/Media/Formats/Audio_concepts
data class AudioSampleMetadata(
    val sampleRateInHz: Int,
    val channelCount: Int,
    val bitPerSample: Int, // 注意单位是bit，8bit=1byte
) {
    // 每个采样点占有空间的字节数(Byte)
    val bytesPerSample: Int = bitPerSample / 8

    companion object {
        // 这个是最常见的配置
        val Default = AudioSampleMetadata(
            sampleRateInHz = 44100,
            channelCount = 2,
            bitPerSample = 16,
        )
    }
}

fun MonoAudioSampleMetadata(
    sampleRateInHz: Int = 44100,
    bitPerSample: Int = 16,
) = AudioSampleMetadata(
    sampleRateInHz, 1, bitPerSample,
)

fun StereoAudioSampleMetadata(
    sampleRateInHz: Int = 44100,
    bitPerSample: Int = 16,
) = AudioSampleMetadata(
    sampleRateInHz, 2, bitPerSample,
)