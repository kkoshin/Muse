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
import okio.Path.Companion.toPath
import okio.buffer
import okio.sink
import okio.use
import org.koin.java.KoinJavaComponent.inject
import java.io.IOException

@OptIn(ExperimentalSugarApi::class)
class ExportViewModel : ViewModel() {

    private val appContext: Context by inject(Context::class.java)

    private val appFileHelper = AppFileHelper(appContext)

    fun testDecodeMp3(mp3Uri: Uri) {
        val pcm = appFileHelper.requireFilesDir(false).resolve("decoded.pcm")
        val wav = appFileHelper.requireFilesDir(false).resolve("decoded.wav")
        val sink = pcm.sink().buffer()
        viewModelScope.launch(Dispatchers.IO) {
            sink.use {
                decodeAudio(mp3Uri) { pcmData ->
                    sink.write(pcmData)
                }
            }
            FileSystem.SYSTEM.copy(pcm.toOkioPath(), wav.toOkioPath())
            WaveHeaderWriter(
                filePath = wav.absolutePath,
                waveConfig = WaveConfig(
                    sampleRate = 44100,
                    channels = 1,
                    compatMode = false
                )
            ).writeHeader()
        }
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