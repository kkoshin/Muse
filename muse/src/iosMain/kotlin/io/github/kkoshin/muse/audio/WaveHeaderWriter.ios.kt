package io.github.kkoshin.muse.audio

import okio.Path

import platform.Foundation.NSFileHandle
import platform.Foundation.NSFileManager
import platform.Foundation.fileHandleForUpdatingAtPath
import platform.Foundation.closeFile
import platform.Foundation.seekToFileOffset
import platform.Foundation.writeData
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual fun writeWaveHeader(
    filePath: Path,
    audioMetadata: AudioSampleMetadata
) {
    val fileManager = NSFileManager.defaultManager
    val pathString = filePath.toString()
    
    val attributes = fileManager.attributesOfItemAtPath(pathString, error = null)
    val fileSize = attributes?.get(platform.Foundation.NSFileSize) as? Long ?: 0L
    
    val totalAudioLen = fileSize - 44
    val totalDataLen = totalAudioLen + 36
    val channels = audioMetadata.channelCount

    val sampleRate = audioMetadata.sampleRateInHz.toLong()
    val byteRate =
        (audioMetadata.bytesPerSample * audioMetadata.sampleRateInHz * channels).toLong()
    
    val header = getWavFileHeaderByteArray(
        totalAudioLen,
        totalDataLen,
        sampleRate,
        channels,
        byteRate,
        audioMetadata.bitPerSample,
    )

    val fileHandle = NSFileHandle.fileHandleForUpdatingAtPath(pathString)
    if (fileHandle != null) {
        fileHandle.seekToFileOffset(0uL)
        header.usePinned { pinned ->
            val data = NSData.create(bytes = pinned.addressOf(0), length = header.size.toULong())
            fileHandle.writeData(data)
        }
        fileHandle.closeFile()
    }
}

private fun getWavFileHeaderByteArray(
    totalAudioLen: Long,
    totalDataLen: Long,
    longSampleRate: Long,
    channels: Int,
    byteRate: Long,
    bitsPerSample: Int,
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