package io.github.kkoshin.muse.feature.export

import kotlin.time.Duration

sealed interface SilenceDuration {
    class Fixed(
        val duration: Duration,
    ) : SilenceDuration

    class Dynamic(
        val min: Duration,
        val durationPerChar: Duration,
    ) : SilenceDuration
}
