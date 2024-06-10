package com.github.kkoshin.muse.export

import android.content.Context
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.foodiestudio.sugar.ExperimentalSugarApi
import com.github.foodiestudio.sugar.storage.AppFileHelper
import com.github.kkoshin.muse.debugLog
import com.github.kkoshin.muse.export.audio.WaveConfig
import com.github.kkoshin.muse.export.audio.WaveHeaderWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import okio.buffer
import okio.sink
import okio.use
import org.koin.java.KoinJavaComponent.inject
import java.io.IOException
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalSugarApi::class)
class ExportViewModel : ViewModel() {
    private val appContext: Context by inject(Context::class.java)

    private val appFileHelper = AppFileHelper(appContext)

    private val volumeBoost = 3f

    fun testDecodeMp3(mp3Uri: Uri) {
        val pcm = appFileHelper.requireFilesDir(false).resolve("decoded.pcm")
        val wav = appFileHelper.requireFilesDir(false).resolve("decoded.wav")
        val sink = pcm.sink().buffer()
        viewModelScope.launch(Dispatchers.IO) {
            sink.use {
                decodeAudio(mp3Uri) { pcmData ->
                    sink.write(boostVolume(pcmData, volumeBoost))
                }
                // 尾部添加3秒静音块，采样率等写死了
                sink.write(getSilence(3.seconds))
            }
            FileSystem.SYSTEM.copy(pcm.toOkioPath(), wav.toOkioPath())
            WaveHeaderWriter(
                filePath = wav.absolutePath,
                waveConfig = WaveConfig(
                    sampleRate = 44100,
                    channels = 1,
                    compatMode = false,
                )
            ).writeHeader()
        }
    }

    fun boostVolume(byteArray: ByteArray, volumeBoost: Float): ByteArray {
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


    private fun getSilence(duration: Duration): ByteArray {
        val sampleRate = 44100

        // Duration of silence in seconds
        val silenceDurationInSeconds = duration.inWholeSeconds.toInt()

        // Number of bytes per sample (16-bit PCM)
        val bytesPerSample = 2

        // Calculate the total number of bytes for the silence duration
        val totalBytesForSilence = sampleRate * silenceDurationInSeconds * bytesPerSample

        // Create a buffer filled with zeros (silence)
        return ByteArray(totalBytesForSilence)
    }

    // mp3 -> pcm
    private suspend fun decodeAudio(mp3Uri: Uri, onDecode: (ByteArray) -> Unit) =
        withContext(Dispatchers.Default) {
            debugLog {
                "start decodeAudio"
            }
            val extractor = MediaExtractor()
            val audioTrackIndex = initMediaExtractor(extractor, mp3Uri)
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
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM
                        )
                        isEOS = true
                    } else {
                        val presentationTimeUs: Long = extractor.getSampleTime()
                        codec.queueInputBuffer(
                            inputBufferIndex,
                            0,
                            sampleSize,
                            presentationTimeUs,
                            0
                        )
                        extractor.advance()
                    }
                }

                val bufferInfo = MediaCodec.BufferInfo()
                var outputBufferIndex = codec.dequeueOutputBuffer(bufferInfo, timeoutUs)
                while (outputBufferIndex >= 0) {
                    debugLog {
                        "outputBufferIndex: $outputBufferIndex"
                    }
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
    private fun initMediaExtractor(extractor: MediaExtractor, input: Uri): Int {
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