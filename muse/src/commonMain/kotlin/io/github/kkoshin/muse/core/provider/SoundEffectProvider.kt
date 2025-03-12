package io.github.kkoshin.muse.core.provider

import java.io.OutputStream
import kotlin.time.Duration

interface SoundEffectProvider {
    suspend fun makeSoundEffects(
        prompt: String,
        config: SoundEffectConfig,
        target: OutputStream
    ): Result<Unit>
}

data class SoundEffectConfig(
    val duration: Duration? = null,
    val promptInfluence: Float = 1f,
)