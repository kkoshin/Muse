package io.github.kkoshin.muse.core.manager

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.kkoshin.elevenlabs.model.SubscriptionStatus
import io.github.kkoshin.muse.core.provider.VoiceProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountManager(private val dataStore: DataStore<Preferences>) {

    // -- Provider-specific API keys --------------------------------------

    private val elevenLabsKey = stringPreferencesKey("elevenLabsApiKey")
    private val sixtydbKey = stringPreferencesKey("sixtydbApiKey")
    private val providerKey = stringPreferencesKey("voiceProvider")
    private val statusKey = stringPreferencesKey("subscriptionStatus")

    /** Kept for backwards compatibility — represents the ElevenLabs key. */
    var apiKey: Flow<String?> = dataStore.data.map { it[elevenLabsKey] }

    val apiKeyConfigured: Flow<Boolean> = dataStore.data.map { !it[elevenLabsKey].isNullOrEmpty() }

    val sixtydbApiKey: Flow<String?> = dataStore.data.map { it[sixtydbKey] }

    val sixtydbApiKeyConfigured: Flow<Boolean> = dataStore.data.map { !it[sixtydbKey].isNullOrEmpty() }

    suspend fun setElevenLabsApiKey(apiKey: String) {
        check(apiKey.isNotBlank()) { "api key cannot be blank" }
        dataStore.edit { it[elevenLabsKey] = apiKey }
    }

    suspend fun setSixtyDbApiKey(apiKey: String) {
        check(apiKey.isNotBlank()) { "api key cannot be blank" }
        dataStore.edit { it[sixtydbKey] = apiKey }
    }

    // -- Active provider selection ---------------------------------------

    val voiceProvider: Flow<VoiceProvider> = dataStore.data.map { prefs ->
        VoiceProvider.fromName(prefs[providerKey])
    }

    suspend fun setVoiceProvider(provider: VoiceProvider) {
        dataStore.edit { it[providerKey] = provider.name }
    }

    // -- Subscription status (ElevenLabs only — 60db has no equivalent) --

    var subscriptionStatus: Flow<SubscriptionStatus?> = dataStore.data.map { prefs ->
        prefs[statusKey]?.let { SubscriptionStatus.valueOf(it) }
    }

    suspend fun setSubscriptionStatus(status: SubscriptionStatus) {
        dataStore.edit { it[statusKey] = status.name }
    }
}
