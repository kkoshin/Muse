package io.github.kkoshin.muse.core.provider

/**
 * Enumerates which TTS/STT backend is active. Persisted via
 * [io.github.kkoshin.muse.core.manager.AccountManager.voiceProvider] in DataStore
 * and consumed by [io.github.kkoshin.muse.core.manager.ProcessorRouter] to pick
 * the underlying implementation at call time.
 */
enum class VoiceProvider {
    /** Default — uses the `:elevenlabs` module client. */
    ELEVENLABS,

    /** Routes TTS/STT/voices through the `:sixtydb` module. Audio isolation
     * and sound generation fall back to ElevenLabs when this is active. */
    SIXTYDB,
    ;

    companion object {
        val DEFAULT = ELEVENLABS

        fun fromName(name: String?): VoiceProvider = entries.firstOrNull { it.name == name } ?: DEFAULT
    }
}
