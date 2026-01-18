package io.github.kkoshin.muse.repo

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class MusePathManagerTest {

    @Test
    fun testGetVoiceDir() {
        val manager = MusePathManager()
        val path = manager.getVoiceDir("test_voice_id")
        
        // Verify directory exists
        @OptIn(ExperimentalForeignApi::class)
        val exists = NSFileManager.defaultManager.fileExistsAtPath(path.toString())
        assertTrue(exists, "Directory should exist")
    }

    @Test
    fun testRelativePaths() {
        val exportPath = MusePathManager.getExportRelativePath()
        assertEquals("Exports", exportPath)

        val musicPath = MusePathManager.getMusicRelativePath()
        assertEquals("Music", musicPath)
    }
}