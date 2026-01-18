package io.github.kkoshin.muse.audio

import io.github.kkoshin.muse.platformbridge.logcat
import io.github.kkoshin.muse.platformbridge.toNsUrl
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.yield
import kotlinx.coroutines.withContext
import okio.BufferedSink
import okio.Path
import platform.AVFoundation.*
import platform.CoreMedia.*
import platform.Foundation.NSURL
import kotlin.math.roundToInt

actual class Mp3Decoder {

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun decodeMp3ToPCM(
        pcmSink: BufferedSink,
        mp3Path: Path,
        volumeBoost: Float
    ) = withContext(Dispatchers.IO) {
        val url = mp3Path.toNsUrl() ?: return@withContext
        val asset = AVURLAsset.URLAssetWithURL(url, options = null)
        
        val reader = try {
            AVAssetReader.assetReaderWithAsset(asset, null)
        } catch (e: Throwable) {
            logcat { "Failed to create AVAssetReader for $url: $e" }
            null
        } ?: return@withContext
        
        val tracks = asset.tracks
        val audioTrack = tracks.filterIsInstance<AVAssetTrack>().firstOrNull { 
            it.mediaType == AVMediaTypeAudio 
        } ?: run {
            logcat { "No audio track found in asset: $url" }
            return@withContext
        }

        // 1819304813 is 'lpcm'
        val kAudioFormatLinearPCM = 1819304813UL

        val settings = mapOf<Any?, Any?>(
            "AVFormatIDKey" to kAudioFormatLinearPCM,
            "AVLinearPCMBitDepthKey" to 16,
            "AVLinearPCMIsBigEndianKey" to false,
            "AVLinearPCMIsFloatKey" to false,
            "AVLinearPCMIsNonInterleaved" to false,
            "AVNumberOfChannelsKey" to 1,
            "AVSampleRateKey" to 44100.0
        )

        val output = AVAssetReaderTrackOutput(track = audioTrack, outputSettings = settings)
        if (reader.canAddOutput(output)) {
            reader.addOutput(output)
        } else {
            logcat { "Cannot add output to reader for $url" }
            return@withContext
        }
        
        if (!reader.startReading()) {
            logcat { "Failed to start reading asset: $url, error: ${reader.error?.localizedDescription}" }
            return@withContext
        }

        while (reader.status == platform.AVFoundation.AVAssetReaderStatusReading) {
            yield()
            val sampleBuffer = output.copyNextSampleBuffer() ?: break
            val blockBuffer = CMSampleBufferGetDataBuffer(sampleBuffer) ?: continue
            val length = CMBlockBufferGetDataLength(blockBuffer)
            
            val byteArray = ByteArray(length.toInt())
            byteArray.usePinned { pinned ->
                CMBlockBufferCopyDataBytes(
                    blockBuffer,
                    0.toULong(),
                    length.toULong(),
                    pinned.addressOf(0)
                )
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
            val high = (byteArray[i + 1].toInt() and 0xff) shl 8
            temp = ((low or high).toShort().toInt() * volumeBoost).roundToInt()
            
            // Clipping to ensure we don't exceed the 16-bit limit
            temp = temp.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            
            // Split the short back into two bytes and store them
            boostedByteArray[i] = (temp and 0xff).toByte()
            boostedByteArray[i + 1] = (temp shr 8 and 0xff).toByte()
        }

        return boostedByteArray
    }
}
