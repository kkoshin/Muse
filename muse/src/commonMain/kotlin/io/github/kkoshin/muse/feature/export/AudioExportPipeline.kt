package io.github.kkoshin.muse.feature.export

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.kkoshin.muse.audio.AudioSampleMetadata
import io.github.kkoshin.muse.audio.MonoAudioSampleMetadata
import io.github.kkoshin.muse.audio.Mp3Encoder
import io.github.kkoshin.muse.audio.Mp3Metadata
import io.github.kkoshin.muse.audio.WavParser
import io.github.kkoshin.muse.audio.WaveHeaderWriter
import io.github.kkoshin.muse.platformbridge.SystemFileSystem
import io.github.kkoshin.muse.platformbridge.createCacheFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import okio.BufferedSink
import okio.Path
import okio.buffer
import okio.use
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.times

@Composable
fun rememberAudioExportPipeline(
    input: List<Path>,
    paddingSilence: Duration = 1.seconds,
): AudioExportPipeline =
    remember {
        AudioExportPipeline(
            input,
            emptyList(),
            SilenceDuration.Fixed(paddingSilence),
        )
    }

/**
 * FIXME 静音块精度目前只能支持到秒
 */
class AudioExportPipeline(
    private val pcmInputs: List<Path>,
    private val phrases: List<String>,
    private val paddingSilence: SilenceDuration,
    private val audioMetadata: AudioSampleMetadata = MonoAudioSampleMetadata(),
) : ExportPipeline<Unit> {
    private val _progress: MutableStateFlow<Int> = MutableStateFlow(-1)
    override val progress: StateFlow<Int> = _progress

    init {
        if (paddingSilence is SilenceDuration.Dynamic) {
            check(pcmInputs.size == phrases.size)
        }
    }

    override suspend fun start(outputSink: BufferedSink): Result<Unit> =
        runCatching {
            check(pcmInputs.isNotEmpty())
            withContext(Dispatchers.IO) {
                val wav = createCacheFile("temp.wav", false)
                SystemFileSystem.sink(wav).buffer().use { sink ->
                    // 将所有的 pcm 文件合并成一个 wav 文件, 并添加适当的静音
                    generateMp3Data(sink)
                }
                // 写入 wav header
                WaveHeaderWriter(
                    filePath = wav,
                    audioMetadata = audioMetadata,
                ).writeHeader()

                // 准备好一个 wav 格式的文件，然后再转码为 mp3
                val wavParser = WavParser(SystemFileSystem.source(wav).buffer())
                encodeWavAsMp3(outputSink, wavParser)
                SystemFileSystem.delete(wav)
                _progress.value = 100
            }
        }.onFailure { e ->
            e.printStackTrace()
            _progress.value = -1
        }

    override fun cancel() {
        _progress.value = -1
    }

    private fun generateMp3Data(sink: BufferedSink) {
        pcmInputs.forEachIndexed { index, filePath ->
            _progress.value = (index / pcmInputs.size.toFloat() * 100).roundToInt()
            sink.writeAll(SystemFileSystem.source(filePath))
            when (paddingSilence) {
                is SilenceDuration.Dynamic -> {
                    val duration =
                        phrases[index].length * paddingSilence.durationPerChar
                    if (duration.inWholeSeconds > 0) {
                        sink.write(
                            getSilence(
                                maxOf(paddingSilence.min, duration),
                                audioMetadata
                            )
                        )
                    }
                }

                is SilenceDuration.Fixed -> {
                    if (paddingSilence.duration.inWholeSeconds > 0) {
                        sink.write(getSilence(paddingSilence.duration, audioMetadata))
                    }
                }
            }
        }
    }

    // 将 wav 文件转换为 mp3 文件
    private suspend fun encodeWavAsMp3(
        outputSink: BufferedSink,
        wavParser: WavParser,
    ) {
        val encoder = Mp3Encoder()
        encoder.encode(
            wavParser,
            outputSink,
            Mp3Metadata(
                id3TagArtist = "μ's",
                id3TagYear = getCurrentYear(),
            )
        )
    }
}

fun getCurrentYear(): String {
    return Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .year
        .toString()
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