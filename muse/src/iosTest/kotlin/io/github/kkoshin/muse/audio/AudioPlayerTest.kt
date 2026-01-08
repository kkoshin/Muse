package io.github.kkoshin.muse.audio

import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.Foundation.NSURL
import kotlin.test.Test
import kotlin.test.assertNotNull

class AudioPlayerTest {

    @Test
    fun testAVPlayerInitialization() {
        val url = NSURL(string = "https://www.google.com") // Dummy URL
        val playerItem = AVPlayerItem(uRL = url)
        val player = AVPlayer(playerItem)
        assertNotNull(player)
        println("AVPlayer initialized successfully")
    }
}
