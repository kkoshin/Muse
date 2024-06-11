package com.github.kkoshin.muse.export

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.foodiestudio.sugar.ExperimentalSugarApi
import com.github.foodiestudio.sugar.storage.AppFileHelper
import com.github.kkoshin.muse.audio.Mp3Decoder
import com.github.kkoshin.muse.audio.WaveConfig
import com.github.kkoshin.muse.audio.WaveHeaderWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import okio.buffer
import okio.sink
import okio.use
import org.koin.java.KoinJavaComponent.inject
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
        val decoder = Mp3Decoder()
        viewModelScope.launch(Dispatchers.IO) {
            sink.use {
                decoder.decodeAudio(appContext, mp3Uri) { pcmData ->
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
                ),
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
}