package com.github.kkoshin.muse.audio

import android.media.AudioFormat
import android.media.AudioRecord

/**
 * Configuration for recording file.
 * @property [sampleRate] the number of samples that audio carried per second.
 * @property [channels] number and position of sound source when the sound is recording(最终保存时的通道数，可能大于2，虽然手机上录制的时候只能录制1路或2路)
 * @property [audioEncoding] size of data per sample.
 * @property [compatMode] whether to use compat mode.(当前机子不支持当前采样率，需要做兼容处理)
 */
internal data class WaveConfig(
    val sampleRate: Int,
    val channels: Int,
    val audioEncoding: Int = AudioFormat.ENCODING_PCM_16BIT,
    val compatMode: Boolean,
) {
    val recordBufferSize = AudioRecord.getMinBufferSize(sampleRate, channels, audioEncoding)

    val bitPerSample: Int
        get() = when (audioEncoding) {
            AudioFormat.ENCODING_PCM_8BIT -> 8
            AudioFormat.ENCODING_PCM_16BIT -> 16
            AudioFormat.ENCODING_PCM_32BIT -> 32
            else -> 16
        }

    companion object {
        const val COMPAT_SAMPLE_RATE = 44100
        val compatRecordBufferSize = AudioRecord.getMinBufferSize(
            COMPAT_SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
    }
}