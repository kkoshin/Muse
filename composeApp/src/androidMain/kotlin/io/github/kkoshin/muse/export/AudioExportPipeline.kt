package io.github.kkoshin.muse.export

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.foodiestudio.sugar.ExperimentalSugarApi
import com.github.foodiestudio.sugar.storage.AppFileHelper
import com.naman14.androidlame.LameBuilder
import io.github.kkoshin.muse.audio.AudioSampleMetadata
import io.github.kkoshin.muse.audio.MonoAudioSampleMetadata
import io.github.kkoshin.muse.audio.Mp3Encoder
import io.github.kkoshin.muse.audio.WavParser
import io.github.kkoshin.muse.audio.WaveHeaderWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import okio.source
import okio.use
import java.util.Calendar
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun rememberAudioExportPipeline(
    context: Context,
    input: List<Uri>,
    paddingSilence: Duration = 1.seconds,
): AudioExportPipeline =
    remember {
        AudioExportPipeline(
            context.applicationContext,
            input,
            paddingSilence,
        )
    }

@OptIn(ExperimentalSugarApi::class)
class AudioExportPipeline(
    private val appContext: Context,
    private val pcmInputs: List<Uri>,
    var paddingSilence: Duration,
    private val audioMetadata: AudioSampleMetadata = MonoAudioSampleMetadata(),
) : ExportPipeline<Unit> {
    private val _progress: MutableStateFlow<Int> = MutableStateFlow(-1)
    override val progress: StateFlow<Int> = _progress

    override suspend fun start(target: Uri): Result<Unit> =
        runCatching {
            check(pcmInputs.isNotEmpty())
            withContext(Dispatchers.IO) {
                val wav =
                    AppFileHelper(appContext).requireCacheDir(false).resolve("temp.wav")
                wav.sink().buffer().use { sink ->
                    pcmInputs.forEachIndexed { index, uri ->
                        _progress.value = (index / pcmInputs.size.toFloat() * 100).roundToInt()
                        sink.writeAll(appContext.contentResolver.openInputStream(uri)!!.source())
                        // 每个词之间加两秒间隔
                        if (paddingSilence.inWholeSeconds > 0) {
                            sink.write(getSilence(paddingSilence, audioMetadata))
                        }
                    }
                }
                // 写入 wav header
                WaveHeaderWriter(
                    filePath = wav.absolutePath,
                    audioMetadata = audioMetadata,
                ).writeHeader()
                // 准备好一个 wav 格式的文件，然后再转码为 mp3
                val wavParser = WavParser(wav.inputStream())
                encodeWavAsMp3(target, wavParser)
                _progress.value = 100
            }
        }.onFailure { e ->
            e.printStackTrace()
            _progress.value = -1
        }

    override fun cancel() {
        _progress.value = -1
    }

    private suspend fun encodeWavAsMp3(target: Uri, wavParser: WavParser) {
        val encoder = Mp3Encoder()
        val outputSink =
            appContext.contentResolver.openOutputStream(target)!!.sink().buffer()
        outputSink.use {
            encoder.encode(
                wavParser,
                outputSink,
                LameBuilder()
                    .setId3tagArtist("μ's")
                    .setId3tagYear(
                        Calendar.getInstance().get(Calendar.YEAR).toString(),
                    )
                    .build(),
            )
        }
    }
}

internal fun getSilence(
    duration: Duration,
    sampleMetadata: AudioSampleMetadata,
): ByteArray {
    // Duration of silence in seconds
    val silenceDurationInSeconds = duration.inWholeSeconds.toInt()

    // Calculate the total number of bytes for the silence duration
    val totalBytesForSilence =
        sampleMetadata.sampleRateInHz * silenceDurationInSeconds * sampleMetadata.bytesPerSample

    // Create a buffer filled with zeros (silence)
    return ByteArray(totalBytesForSilence)
}