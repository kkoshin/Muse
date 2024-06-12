package com.github.kkoshin.muse.audio

import android.content.Context
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import com.github.kkoshin.muse.debugLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.BufferedSink
import java.io.IOException
import kotlin.math.roundToInt

/**
 * 将 MP3 文件编码为 PCM
 */
class Mp3Decoder {
    suspend fun decodeMp3ToPCM(
        appContext: Context,
        pcmSink: BufferedSink,
        mp3Uri: Uri,
        volumeBoost: Float = 1.0f,
    ) {
        decodeAudio(appContext, mp3Uri) { pcmData ->
            if (volumeBoost == 1.0f) {
                pcmSink.write(pcmData)
            } else {
                pcmSink.write(boostVolumeFor16BitAudio(pcmData, volumeBoost))
            }
        }
    }

    /**
     * 仅考虑 16bit PCM
     */
    private fun boostVolumeFor16BitAudio(byteArray: ByteArray, volumeBoost: Float): ByteArray {
        val boostedByteArray = ByteArray(byteArray.size)
        var temp: Int

        // Assuming 16-bit PCM, we process 2 bytes at a time
        for (i in byteArray.indices step 2) {
            // Combine two bytes to form a short and apply the volume boost
            temp =
                (((byteArray[i].toInt() and 0xff) or (byteArray[i + 1].toInt() shl 8)) * volumeBoost).roundToInt()
            // Clipping to ensure we don't exceed the 16-bit limit
            temp = temp.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            // Split the short back into two bytes and store them
            boostedByteArray[i] = (temp and 0xff).toByte()
            boostedByteArray[i + 1] = (temp shr 8 and 0xff).toByte()
        }

        return boostedByteArray
    }

    suspend fun decodeAudio(
        appContext: Context,
        mp3Uri: Uri,
        onDecode: (ByteArray) -> Unit,
    ) = withContext(Dispatchers.Default) {
        debugLog {
            "start decodeAudio"
        }
        val extractor = MediaExtractor()
        val audioTrackIndex = initMediaExtractor(appContext, extractor, mp3Uri)
        val format: MediaFormat = extractor.getTrackFormat(audioTrackIndex)
        val mime = format.getString(MediaFormat.KEY_MIME)
        val codec = MediaCodec.createDecoderByType(mime!!)

        codec.configure(format, null,  /* surface */null,  /* crypto */0 /* flags */)
        codec.start()

        val inputBuffers = codec.inputBuffers
        val outputBuffers = codec.outputBuffers

        // Loop to feed input data and retrieve decoded output data
        var isEOS = false
        val timeoutUs: Long = 10000

        while (!isEOS) {
            val inputBufferIndex = codec.dequeueInputBuffer(timeoutUs)
            if (inputBufferIndex >= 0) {
                val inputBuffer = inputBuffers[inputBufferIndex]
                val sampleSize: Int = extractor.readSampleData(inputBuffer, 0)
                if (sampleSize < 0) {
                    // End of stream
                    codec.queueInputBuffer(
                        inputBufferIndex,
                        0,
                        0,
                        0,
                        MediaCodec.BUFFER_FLAG_END_OF_STREAM,
                    )
                    isEOS = true
                } else {
                    val presentationTimeUs: Long = extractor.getSampleTime()
                    codec.queueInputBuffer(
                        inputBufferIndex,
                        0,
                        sampleSize,
                        presentationTimeUs,
                        0,
                    )
                    extractor.advance()
                }
            }

            val bufferInfo = MediaCodec.BufferInfo()
            var outputBufferIndex = codec.dequeueOutputBuffer(bufferInfo, timeoutUs)
            while (outputBufferIndex >= 0) {
                val outputBuffer = outputBuffers[outputBufferIndex]
                val pcmData = ByteArray(bufferInfo.size)
                outputBuffer[pcmData]
                outputBuffer.clear()
                onDecode(pcmData)
                // Here, outputBuffer is a PCM data
                // Process PCM data (e.g., play it, or save it to a file)
                codec.releaseOutputBuffer(outputBufferIndex, false)
                outputBufferIndex = codec.dequeueOutputBuffer(bufferInfo, timeoutUs)
            }
        }
        debugLog {
            "finish decodeAudio"
        }
        codec.stop()
        codec.release()
        extractor.release()
    }

    // Extract Audio Data from MP3
    private fun initMediaExtractor(
        appContext: Context,
        extractor: MediaExtractor,
        input: Uri,
    ): Int {
        try {
            extractor.setDataSource(appContext, input, null) // path to your MP3 file
            var audioTrackIndex = -1
            for (i in 0 until extractor.trackCount) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)
                if (mime!!.startsWith("audio/")) {
                    audioTrackIndex = i
                    break
                }
            }
            if (audioTrackIndex == -1) throw IOException("No audio track found in file.")
            extractor.selectTrack(audioTrackIndex)
            return audioTrackIndex
        } catch (e: IOException) {
            e.printStackTrace()
            return -1
        }
    }
}