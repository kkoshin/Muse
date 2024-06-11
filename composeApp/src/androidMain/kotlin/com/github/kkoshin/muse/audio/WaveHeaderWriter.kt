package com.github.kkoshin.muse.audio

import okio.IOException
import java.io.File
import java.io.RandomAccessFile

internal class WaveHeaderWriter(private val filePath: String, private val waveConfig: WaveConfig) {

    @Throws(IOException::class)
    fun writeHeader() {
        val inputStream = File(filePath).inputStream()
        val totalAudioLen = inputStream.channel.size() - 44
        val totalDataLen = totalAudioLen + 36
        val channels = waveConfig.channels

        val sampleRate = waveConfig.sampleRate.toLong()
        val byteRate =
            (waveConfig.bitPerSample * waveConfig.sampleRate * channels / 8).toLong()
        val header = getWavFileHeaderByteArray(
            totalAudioLen,
            totalDataLen,
            sampleRate,
            channels,
            byteRate,
            waveConfig.bitPerSample
        )
        RandomAccessFile(File(filePath), "rw").use {
            it.seek(0)
            it.write(header)
        }
    }

    private fun getWavFileHeaderByteArray(
        totalAudioLen: Long, totalDataLen: Long, longSampleRate: Long,
        channels: Int, byteRate: Long, bitsPerSample: Int
    ): ByteArray {
        val header = ByteArray(44)
        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = (totalDataLen shr 8 and 0xff).toByte()
        header[6] = (totalDataLen shr 16 and 0xff).toByte()
        header[7] = (totalDataLen shr 24 and 0xff).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (longSampleRate and 0xff).toByte()
        header[25] = (longSampleRate shr 8 and 0xff).toByte()
        header[26] = (longSampleRate shr 16 and 0xff).toByte()
        header[27] = (longSampleRate shr 24 and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()
        header[32] = (channels * (bitsPerSample / 8)).toByte()
        header[33] = 0
        header[34] = bitsPerSample.toByte()
        header[35] = 0
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (totalAudioLen and 0xff).toByte()
        header[41] = (totalAudioLen shr 8 and 0xff).toByte()
        header[42] = (totalAudioLen shr 16 and 0xff).toByte()
        header[43] = (totalAudioLen shr 24 and 0xff).toByte()
        return header
    }
}