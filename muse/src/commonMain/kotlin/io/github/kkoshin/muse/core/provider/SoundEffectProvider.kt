package io.github.kkoshin.muse.core.provider

import okio.Sink
import kotlin.time.Duration

interface SoundEffectProvider {
    suspend fun makeSoundEffects(
        prompt: String,
        config: SoundEffectConfig,
        target: Sink
    ): Result<Unit>
}

data class SoundEffectConfig(
    val duration: Duration? = null,
    val promptInfluence: Float = 1f,
)