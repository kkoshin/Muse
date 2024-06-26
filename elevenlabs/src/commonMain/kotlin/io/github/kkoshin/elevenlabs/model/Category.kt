package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * export const Category = {
 *     Generated: "generated",
 *     Professional: "professional",
 *     HighQuality: "high_quality",
 * } as const;
 */
@Serializable
enum class Category {
    @SerialName("generated")
    Generated,

    @SerialName("professional")
    Professional,

    @SerialName("high_quality")
    HighQuality,
}