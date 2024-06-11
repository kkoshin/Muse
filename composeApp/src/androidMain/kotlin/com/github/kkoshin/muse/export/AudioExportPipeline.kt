package com.github.kkoshin.muse.export

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.kkoshin.muse.audio.Mp3Encoder
import com.github.kkoshin.muse.audio.WavParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import kotlin.math.roundToInt

@Composable
fun rememberAudioExportPipeline(
    context: Context,
    input: List<Uri>,
): AudioExportPipeline =
    remember {
        AudioExportPipeline(
            context.applicationContext,
            input,
        )
    }

class AudioExportPipeline(
    private val context: Context,
    private val wavInputs: List<Uri>,
) : ExportPipeline<Unit> {
    private val _progress: MutableStateFlow<Int> = MutableStateFlow(0)
    override val progress: StateFlow<Int> = _progress

    override suspend fun start(target: Uri): Result<Unit> =
        runCatching {
            check(wavInputs.isNotEmpty())
            val encoder = Mp3Encoder()
            val outputSink =
                context.contentResolver.openOutputStream(target)!!.sink().buffer()
            withContext(Dispatchers.IO) {
                outputSink.use {
                    wavInputs.forEachIndexed { index, uri ->
                        val wavParser = WavParser(context.contentResolver.openInputStream(uri)!!)
                        encoder.encode(wavParser, outputSink)
                        _progress.value = (index / wavInputs.size.toFloat() * 100).roundToInt()
                    }
                    _progress.value = 99
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
}