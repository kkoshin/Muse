package io.github.kkoshin.fancy.config

import kotlinx.serialization.Serializable

@Serializable
data class FancyConfig(
    val referenceWidth: Float,
    val referenceHeight: Float,
    val referenceFontSize: Float
)
