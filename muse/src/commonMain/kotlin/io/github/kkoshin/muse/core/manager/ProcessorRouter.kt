package io.github.kkoshin.muse.core.manager

import io.github.kkoshin.elevenlabs.model.SpeechToTextChunkResponseModel
import io.github.kkoshin.muse.core.provider.AudioIsolationProvider
import io.github.kkoshin.muse.core.provider.CharacterQuota
import io.github.kkoshin.muse.core.provider.STTProvider
import io.github.kkoshin.muse.core.provider.SoundEffectConfig
import io.github.kkoshin.muse.core.provider.SoundEffectProvider
import io.github.kkoshin.muse.core.provider.TTSProvider
import io.github.kkoshin.muse.core.provider.TTSResult
import io.github.kkoshin.muse.core.provider.Voice
import io.github.kkoshin.muse.core.provider.VoiceProvider
import kotlinx.coroutines.flow.first
import okio.Sink
import okio.Source

/**
 * Front for the four provider interfaces. Reads the active [VoiceProvider]
 * from [AccountManager] on every call and dispatches to the matching
 * processor. Lets the rest of the codebase stay provider-agnostic.
 *
 * Feature-parity notes:
 * - TTS, voices, STT: routed straight to the chosen provider.
 * - Audio isolation + sound effects: 60db has no equivalents. When 60db is
 *   the active provider, these calls fall back to ElevenLabs if its key is
 *   set; otherwise they return the underlying processor's
 *   `UnsupportedOperationException`.
 * - Quota: ElevenLabs returns a real character quota; 60db returns
 *   [CharacterQuota.unknown] (its billing is per-request, not character-based).
 */
class ProcessorRouter(
    private val accountManager: AccountManager,
    private val elevenLabs: ElevenLabProcessor,
    private val sixtydb: SixtyDbProcessor,
) : TTSProvider, AudioIsolationProvider, SoundEffectProvider, STTProvider {

    private suspend fun activeTts(): TTSProvider = when (accountManager.voiceProvider.first()) {
        VoiceProvider.ELEVENLABS -> elevenLabs
        VoiceProvider.SIXTYDB -> sixtydb
    }

    private suspend fun activeStt(): STTProvider = when (accountManager.voiceProvider.first()) {
        VoiceProvider.ELEVENLABS -> elevenLabs
        VoiceProvider.SIXTYDB -> sixtydb
    }

    /**
     * Audio isolation + sound effects route to whatever provider supports
     * them. Since only ElevenLabs does, we go there as long as the key is
     * configured — regardless of the user's active "voice" choice.
     */
    private suspend fun fallbackProvider(): AudioIsolationProvider = elevenLabs

    private suspend fun fallbackSoundProvider(): SoundEffectProvider = elevenLabs

    // ---- TTS -----------------------------------------------------------

    override suspend fun generate(voiceId: String, text: String): Result<TTSResult> =
        activeTts().generate(voiceId, text)

    override suspend fun queryQuota(): Result<CharacterQuota> = activeTts().queryQuota()

    override suspend fun queryVoices(): Result<List<Voice>> = activeTts().queryVoices()

    // ---- STT -----------------------------------------------------------

    override suspend fun transcribeAudio(
        audio: Source,
        audioName: String,
    ): Result<SpeechToTextChunkResponseModel> = activeStt().transcribeAudio(audio, audioName)

    // ---- Audio isolation (ElevenLabs-only feature) ---------------------

    override suspend fun removeBackgroundNoise(
        audio: Source,
        audioName: String,
    ): Result<ByteArray> = fallbackProvider().removeBackgroundNoise(audio, audioName)

    // ---- Sound effects (ElevenLabs-only feature) -----------------------

    override suspend fun makeSoundEffects(
        prompt: String,
        config: SoundEffectConfig,
        target: Sink,
    ): Result<Unit> = fallbackSoundProvider().makeSoundEffects(prompt, config, target)
}
