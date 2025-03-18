package io.github.kkoshin.muse.tts.vendor

import io.github.kkoshin.muse.core.provider.CharacterQuota
import io.github.kkoshin.muse.core.provider.TTSProvider
import io.github.kkoshin.muse.core.provider.TTSResult
import io.github.kkoshin.muse.core.provider.Voice

class GroupedTTSProvider(
    private val providers: List<TTSProvider>,
) : TTSProvider {
    override suspend fun generate(
        voiceId: String,
        text: String,
    ): Result<TTSResult> {
        providers.forEachIndexed { index, ttsProvider ->
            if (index == providers.lastIndex) {
                return ttsProvider.generate(voiceId, text)
            }
            val result = ttsProvider.generate(voiceId, text)
            if (result.isSuccess) {
                return result
            }
        }
        return Result.failure(IllegalStateException("All providers failed."))
    }

    override suspend fun queryQuota(): Result<CharacterQuota> {
        var result = CharacterQuota.empty
        providers.forEach {
            result += it.queryQuota().getOrDefault(CharacterQuota.empty)
        }
        return Result.success(result)
    }

    override suspend fun queryVoices(): Result<List<Voice>> {
        providers.forEachIndexed { index, ttsProvider ->
            if (index == providers.lastIndex) {
                return ttsProvider.queryVoices()
            }
            val result = ttsProvider.queryVoices()
            if (result.isSuccess) {
                return result
            }
        }
        return Result.failure(IllegalStateException("All providers failed."))
    }
}