package io.github.kkoshin.muse.audio

import cocoapods.lame.lame_init
import cocoapods.lame.lame_close
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNotNull

import io.github.kkoshin.muse.platformbridge.SystemFileSystem
import okio.Path.Companion.toPath
import okio.buffer
import okio.use
import kotlin.test.assertTrue

class Mp3Test {

    @OptIn(ExperimentalForeignApi::class)
    @Test
    fun testLameLinking() = runTest {
        // Explicitly call a LAME function to verify it is linked correctly.
        val lame = lame_init()
        assertNotNull(lame, "lame_init should return a valid pointer")
        lame_close(lame)
    }

    @Test
    fun testMp3EncoderCreation() {
        val encoder = Mp3Encoder()
        assertNotNull(encoder)
    }

    @Test
    fun testMp3DecoderCreation() {
        val decoder = Mp3Decoder()
        assertNotNull(decoder)
    }
}
