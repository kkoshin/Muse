package io.github.kkoshin.muse.audio

import cocoapods.lame.lame_close
import cocoapods.lame.lame_encode_buffer
import cocoapods.lame.lame_encode_flush
import cocoapods.lame.lame_init
import cocoapods.lame.lame_init_params
import cocoapods.lame.lame_set_in_samplerate
import cocoapods.lame.lame_set_num_channels
import kotlinx.cinterop.CPointed
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CoroutineScope
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
    private suspend fun CoroutineScope.writeMonoAudio(
        wavParser: WavParser,
        lame: CPointer<out CPointed>?,
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
                        // lame_encode_buffer uses CPointer<lame_global_struct> usually, 
                        // which is what lame_init returns.
                        val encodedLength = lame_encode_buffer(
                            lame?.reinterpret(),
                            pinnedBuffer.addressOf(0),
                            pinnedBuffer.addressOf(0),
                            samplesRead,
                            pinnedMp3Buf.addressOf(0).reinterpret(),
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
    private suspend fun CoroutineScope.writeStereoAudio(
        wavParser: WavParser,
        lame: CPointer<out CPointed>?,
        outputSink: BufferedSink,
    ) {
        val bufferL = ShortArray(CHUNK_SIZE)
        val bufferR = ShortArray(CHUNK_SIZE)
        val mp3Buf = ByteArray(CHUNK_SIZE)
        var samplesRead = 0
        do {
            ensureActive()
            samplesRead = wavParser.readStereo(bufferL, bufferR, CHUNK_SIZE)
            if (samplesRead > 0) {
                bufferL.usePinned { pinnedL ->
                    bufferR.usePinned { pinnedR ->
                        mp3Buf.usePinned { pinnedMp3 ->
                            val encodedLength = lame_encode_buffer(
                                lame?.reinterpret(),
                                pinnedL.addressOf(0),
                                pinnedR.addressOf(0),
                                samplesRead,
                                pinnedMp3.addressOf(0).reinterpret(),
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
        lame: CPointer<out CPointed>?,
        outputSink: BufferedSink
    ) {
        val mp3Buf = ByteArray(CHUNK_SIZE)
        mp3Buf.usePinned { pinnedMp3 ->
            val encodedLength = lame_encode_flush(lame?.reinterpret(), pinnedMp3.addressOf(0).reinterpret(), CHUNK_SIZE)
            if (encodedLength > 0) {
                outputSink.write(mp3Buf, 0, encodedLength)
            }
        }
    }
}
