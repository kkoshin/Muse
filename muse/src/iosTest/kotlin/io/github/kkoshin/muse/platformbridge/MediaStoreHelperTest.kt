package io.github.kkoshin.muse.platformbridge

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import kotlin.test.Test
import kotlin.test.assertTrue

class MediaStoreHelperTest {

    @OptIn(ExperimentalForeignApi::class)
    @Test
    fun testExportFileToDownload() {
        val helper = MediaStoreHelper()
        val fileName = "test_export.mp3"
        val relativePath = "TestExports"
        
        val path = helper.exportFileToDownload(fileName, relativePath)
        
        // 1. Verify the path contains the file name and relative path
        assertTrue(path.toString().contains(fileName), "Path should contain file name")
        assertTrue(path.toString().contains(relativePath), "Path should contain relative path")
        
        // 2. Verify the directory was created
        val fileManager = NSFileManager.defaultManager
        val documentsDirectory = fileManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        val expectedDir = documentsDirectory!!.URLByAppendingPathComponent(relativePath, isDirectory = true)
        
        assertTrue(fileManager.fileExistsAtPath(expectedDir!!.path!!), "Target directory should exist")
    }
}
