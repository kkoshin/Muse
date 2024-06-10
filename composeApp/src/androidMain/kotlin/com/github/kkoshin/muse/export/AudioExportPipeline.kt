package com.github.kkoshin.muse.export

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.naman14.androidlame.AndroidLame
import com.naman14.androidlame.LameBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import java.io.InputStream

@Composable
fun rememberAudioExportPipeline(
    context: Context,
    input: Uri,
): AudioExportPipeline =
    remember {
        AudioExportPipeline(
            context.applicationContext,
            context.contentResolver.openInputStream(input)!!,
        )
    }

class AudioExportPipeline(
    private val context: Context,
    private val wavInput: InputStream,
) : ExportPipeline<Unit> {
    private val CHUNK_SIZE = 8192
    private val _progress: MutableStateFlow<Int> = MutableStateFlow(0)
    override val progress: StateFlow<Int> = _progress
    override suspend fun start(target: Uri): Result<Unit> =
        runCatching {
            withContext(Dispatchers.IO) {
                val wavParser = WavParser(wavInput)
                wavParser.openWave()
                val lame = LameBuilder()
                    .setInSampleRate(wavParser.getSampleRate())
                    .setOutChannels(wavParser.getChannels())
                    .setOutBitrate(128)
                    .setOutSampleRate(wavParser.getSampleRate())
                    .setQuality(5)
                    .build()
                _progress.value = 10
                val outputSink = context.contentResolver.openOutputStream(target)!!.sink().buffer()
                when (wavParser.getChannels()) {
                    2 -> writeStereoAudio(wavParser, lame) { mp3Buf, encodedLength ->
                        ensureActive()
                        outputSink.write(mp3Buf, 0, encodedLength)
                    }

                    else -> writeMonoAudio(wavParser, lame) { mp3Buf, encodedLength ->
                        ensureActive()
                        outputSink.write(mp3Buf, 0, encodedLength)
                    }
                }
                _progress.value = 0
            }
        }.onFailure { e ->
            e.printStackTrace()
            _progress.value = 0
        }

    override fun cancel() {
        _progress.value = 0
    }

    // 单通道
    private fun writeMonoAudio(
        wavParser: WavParser,
        lame: AndroidLame,
        onEncode: (buffer: ByteArray, length: Int) -> Unit,
    ) {
        val buffer = ShortArray(CHUNK_SIZE)
        val mp3Buf = ByteArray(CHUNK_SIZE)
        var bytesRead = 0
        do {
            // read source data
            bytesRead = wavParser.read(buffer, CHUNK_SIZE)
            if (bytesRead > 0) {
                // encode data
                lame.encode(buffer, buffer, bytesRead, mp3Buf).let {
                    if (it > 0) {
                        onEncode(mp3Buf, it)
                    }
                }
            }
        } while (bytesRead > 0)
        lame.flush(mp3Buf).let {
            if (it > 0) {
                onEncode(mp3Buf, it)
            }
        }
        lame.close()
    }

    // 双通道（立体声）
    private fun writeStereoAudio(
        wavParser: WavParser,
        lame: AndroidLame,
        onEncode: (buffer: ByteArray, length: Int) -> Unit,
    ) {
        val bufferL = ShortArray(CHUNK_SIZE)
        val bufferR = ShortArray(CHUNK_SIZE)
        val mp3Buf = ByteArray(CHUNK_SIZE)
        var bytesRead = 0
        do {
            // read source data
            bytesRead = wavParser.read(bufferL, bufferR, CHUNK_SIZE)
            if (bytesRead > 0) {
                // encode data
                lame.encode(bufferL, bufferR, bytesRead, mp3Buf).let {
                    if (it > 0) {
                        onEncode(mp3Buf, it)
                    }
                }
            }
        } while (bytesRead > 0)
        lame.flush(mp3Buf).let {
            if (it > 0) {
                onEncode(mp3Buf, it)
            }
        }
        lame.close()
    }
}