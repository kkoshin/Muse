package io.github.kkoshin.muse.audio

import com.naman14.androidlame.AndroidLame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okio.BufferedSink

class Mp3Encoder {
    private val CHUNK_SIZE = 8192

    suspend fun encode(
        wavParser: WavParser,
        outputSink: BufferedSink,
        lame: AndroidLame = AndroidLame(),
    ) = withContext(Dispatchers.IO) {
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