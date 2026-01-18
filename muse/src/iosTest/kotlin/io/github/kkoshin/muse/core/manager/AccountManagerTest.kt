package io.github.kkoshin.muse.core.manager

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import platform.Foundation.NSTemporaryDirectory
import kotlin.test.Test
import kotlin.test.assertEquals

class AccountManagerTest {

    @Test
    fun testApiKeyPersistence() = runTest {
        val tempDir = NSTemporaryDirectory() + "test_account.preferences_pb"
        val dataStore = PreferenceDataStoreFactory.createWithPath(produceFile = { tempDir.toPath() })
        val accountManager = AccountManager(dataStore)
        
        val testApiKey = "test_api_key_123"
        accountManager.setElevenLabsApiKey(testApiKey)
        
        val savedApiKey = accountManager.apiKey.first()
        assertEquals(testApiKey, savedApiKey)
    }
}
