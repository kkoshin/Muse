package io.github.kkoshin.muse.core.manager

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.kkoshin.elevenlabs.model.SubscriptionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountManager(private val dataStore: DataStore<Preferences>) {

    private val key = stringPreferencesKey("elevenLabsApiKey")

    private val statusKey = stringPreferencesKey("subscriptionStatus")

    var apiKey: Flow<String?> = dataStore.data.map { preferences ->
        preferences[key]
    }

    suspend fun setElevenLabsApiKey(apiKey: String) {
        check(apiKey.isNotBlank()) { "api key cannot be blank" }
        dataStore.edit { preferences ->
            preferences[key] = apiKey
        }
    }

    var subscriptionStatus: Flow<SubscriptionStatus?> = dataStore.data.map { preferences ->
        preferences[statusKey]?.let {
            SubscriptionStatus.valueOf(it)
        }
    }

    suspend fun setSubscriptionStatus(status: SubscriptionStatus) {
        dataStore.edit { preferences ->
            preferences[statusKey] = status.name
        }
    }
}
