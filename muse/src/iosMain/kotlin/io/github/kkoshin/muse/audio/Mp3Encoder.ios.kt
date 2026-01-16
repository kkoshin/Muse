package io.github.kkoshin.muse.audio

import cocoapods.lame.lame_close
import cocoapods.lame.lame_encode_buffer
import cocoapods.lame.lame_encode_buffer_interleaved
import cocoapods.lame.lame_encode_flush
import cocoapods.lame.lame_init
import cocoapods.lame.lame_init_params
import cocoapods.lame.lame_set_in_samplerate
import cocoapods.lame.lame_set_num_channels
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okio.BufferedSink

actual class Mp3Encoder {
    private val CHUNK_SIZE = 8192

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun encode(
        wavParser: WavParser,
        outputSink: BufferedSink,
        metadata: Mp3Metadata
    ) {
        val lame = lame_init()
        lame_set_in_samplerate(lame, wavParser.sampleRate)
        lame_set_num_channels(lame, wavParser.channels)
        // TODO: Set ID3 tags if possible via lame C API
        lame_init_params(lame)

        try {
            withContext(Dispatchers.IO) {
                if (wavParser.channels == 2) {
                    writeStereoAudio(wavParser, lame, outputSink)
                } else {
                    writeMonoAudio(wavParser, lame, outputSink)
                }
            }
        } finally {
            lame_close(lame)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private suspend fun writeMonoAudio(
        wavParser: WavParser,
        lame: kotlinx.cinterop.CPointer<out kotlinx.cinterop.CPointed>?,
        outputSink: BufferedSink,
    ) {
        val buffer = ShortArray(CHUNK_SIZE)
        val mp3Buf = ByteArray(CHUNK_SIZE)
        var samplesRead = 0
        do {
            ensureActive()
            samplesRead = wavParser.readMono(buffer, CHUNK_SIZE)
            if (samplesRead > 0) {
                buffer.usePinned { pinnedBuffer ->
                    mp3Buf.usePinned { pinnedMp3Buf ->
                        val encodedLength = lame_encode_buffer(
                            lame,
                            pinnedBuffer.addressOf(0),
                            pinnedBuffer.addressOf(0), // Lame mono expects both or uses it as such
                            samplesRead,
                            pinnedMp3Buf.addressOf(0),
                            CHUNK_SIZE
                        )
                        if (encodedLength > 0) {
                            outputSink.write(mp3Buf, 0, encodedLength)
                        }
                    }
                }
            }
        } while (samplesRead > 0)

        flushLame(lame, outputSink)
    }

    @OptIn(ExperimentalForeignApi::class)
    private suspend fun writeStereoAudio(
        wavParser: WavParser,
        lame: kotlinx.cinterop.CPointer<out kotlinx.cinterop.CPointed>?,
        outputSink: BufferedSink,
    ) {
        val bufferL = ShortArray(CHUNK_SIZE)
        val bufferR = ShortArray(CHUNK_SIZE)
        val mp3Buf = ByteArray(CHUNK_SIZE)
        // Lame can take interleaved or separate. WavParser provides separate in readStereo.
        var samplesRead = 0
        do {
            ensureActive()
            samplesRead = wavParser.readStereo(bufferL, bufferR, CHUNK_SIZE)
            if (samplesRead > 0) {
                bufferL.usePinned { pinnedL ->
                    bufferR.usePinned { pinnedR ->
                        mp3Buf.usePinned { pinnedMp3 ->
                            val encodedLength = lame_encode_buffer(
                                lame,
                                pinnedL.addressOf(0),
                                pinnedR.addressOf(0),
                                samplesRead,
                                pinnedMp3.addressOf(0),
                                CHUNK_SIZE
                            )
                            if (encodedLength > 0) {
                                outputSink.write(mp3Buf, 0, encodedLength)
                            }
                        }
                    }
                }
            }
        } while (samplesRead > 0)

        flushLame(lame, outputSink)
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun flushLame(
        lame: kotlinx.cinterop.CPointer<out kotlinx.cinterop.CPointed>?,
        outputSink: BufferedSink
    ) {
        val mp3Buf = ByteArray(CHUNK_SIZE)
        mp3Buf.usePinned { pinnedMp3 ->
            val encodedLength = lame_encode_flush(lame, pinnedMp3.addressOf(0), CHUNK_SIZE)
            if (encodedLength > 0) {
                outputSink.write(mp3Buf, 0, encodedLength)
            }
        }
    }
}
