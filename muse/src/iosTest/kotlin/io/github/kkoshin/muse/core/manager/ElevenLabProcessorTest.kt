package io.github.kkoshin.muse.core.manager

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import io.github.kkoshin.muse.core.provider.TTSProvider
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import platform.Foundation.NSTemporaryDirectory
import kotlin.test.Test
import kotlin.test.assertNotNull

class ElevenLabProcessorTest {

    @Test
    fun testInitialization() = runTest {
        val tempDir = NSTemporaryDirectory() + "test_datastore.preferences_pb"
        val dataStore = PreferenceDataStoreFactory.createWithPath(produceFile = { tempDir.toPath() })
        val accountManager = AccountManager(dataStore)
        val scope = MainScope()
        
        val processor = ElevenLabProcessor(accountManager, scope)
        assertNotNull(processor)
        
        // Since we don't have a real API key in tests, we just verify it can be instantiated
        // and doesn't crash on setup.
    }
}
