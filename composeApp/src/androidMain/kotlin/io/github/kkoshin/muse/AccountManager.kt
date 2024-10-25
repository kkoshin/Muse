package io.github.kkoshin.muse

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountManager(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "account")

    private val key = stringPreferencesKey("elevenLabsApiKey")

    var apiKey: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[key]
    }

    suspend fun setElevenLabsApiKey(apiKey: String) {
        check(apiKey.isNotBlank()) { "api key cannot be blank" }
        context.dataStore.edit { preferences ->
            preferences[key] = apiKey
        }
    }
}