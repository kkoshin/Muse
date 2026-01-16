package io.github.kkoshin.muse.audio

import io.github.kkoshin.muse.platformbridge.toNsUrl
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.get
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.yield
import kotlinx.coroutines.withContext
import okio.BufferedSink
import okio.Path
import platform.AVFoundation.AVAsset
import platform.AVFoundation.AVAssetReader
import platform.AVFoundation.AVAssetReaderTrackOutput
import platform.AVFoundation.AVMediaTypeAudio
import platform.AVFoundation.assetReaderWithAsset
import platform.AudioToolbox.kAudioFormatLinearPCM
import platform.CoreAudio.kAudioFormatFlagIsSignedInteger
import platform.CoreMedia.CMSampleBufferGetDataBuffer
import platform.CoreMedia.CMSampleBufferGetNumSamples
import platform.CoreMedia.CMBlockBufferCopyDataBytes
import platform.CoreMedia.CMBlockBufferGetDataLength
import platform.Foundation.NSNumber
import kotlin.math.roundToInt

actual class Mp3Decoder {

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun decodeMp3ToPCM(
        pcmSink: BufferedSink,
        mp3Path: Path,
        volumeBoost: Float
    ) = withContext(Dispatchers.IO) {
        val url = mp3Path.toNsUrl() ?: return@withContext
        val asset = AVAsset.assetWithURL(url)
        
        val reader = AVAssetReader.assetReaderWithAsset(asset, null)
        val audioTrack = asset.tracksWithMediaType(AVMediaTypeAudio).firstOrNull() as? platform.AVFoundation.AVAssetTrack
            ?: return@withContext

        val settings = mapOf<Any?, Any?>(
            platform.AVFoundation.AVFormatIDKey to kAudioFormatLinearPCM,
            platform.AVFoundation.AVLinearPCMBitDepthKey to 16,
            platform.AVFoundation.AVLinearPCMIsBigEndianKey to false,
            platform.AVFoundation.AVLinearPCMIsFloatKey to false,
            platform.AVFoundation.AVLinearPCMIsNonInterleaved to false,
            platform.AVFoundation.AVNumberOfChannelsKey to 1,
            platform.AVFoundation.AVSampleRateKey to 44100.0
        )

        val output = AVAssetReaderTrackOutput(audioTrack, settings)
        reader.addOutput(output)
        reader.startReading()

        while (reader.status == platform.AVFoundation.AVAssetReaderStatusReading) {
            yield()
            val sampleBuffer = output.copyNextSampleBuffer() ?: break
            val blockBuffer = CMSampleBufferGetDataBuffer(sampleBuffer) ?: continue
            val length = CMBlockBufferGetDataLength(blockBuffer)
            
            val byteArray = ByteArray(length.toInt())
            byteArray.usePinned { pinned ->
                CMBlockBufferCopyDataBytes(blockBuffer, 0, length, pinned.addressOf(0))
            }

            if (volumeBoost == 1.0f) {
                pcmSink.write(byteArray)
            } else {
                pcmSink.write(boostVolumeFor16BitAudio(byteArray, volumeBoost))
            }
            
            platform.CoreFoundation.CFRelease(sampleBuffer)
        }
        
        pcmSink.flush()
    }

    private fun boostVolumeFor16BitAudio(byteArray: ByteArray, volumeBoost: Float): ByteArray {
        val boostedByteArray = ByteArray(byteArray.size)
        var temp: Int

        // Assuming 16-bit PCM, we process 2 bytes at a time
        for (i in byteArray.indices step 2) {
            if (i + 1 >= byteArray.size) break
            // Combine two bytes to form a short and apply the volume boost
            val low = byteArray[i].toInt() and 0xff
            val high = byteArray[i + 1].toInt() shl 8
            temp = ((low or high) * volumeBoost).roundToInt()
            
            // Clipping to ensure we don't exceed the 16-bit limit
            temp = temp.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            
            // Split the short back into two bytes and store them
            boostedByteArray[i] = (temp and 0xff).toByte()
            boostedByteArray[i + 1] = (temp shr 8 and 0xff).toByte()
        }

        return boostedByteArray
    }
}
