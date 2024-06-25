package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * export type Currency = "usd" | "eur";
 *
 * export const Currency = {
 *     Usd: "usd",
 *     Eur: "eur",
 * } as const;
 */
@Serializable
enum class Currency {
    @SerialName("usd")
    Usd,

    @SerialName("eur")
    Eur,
}
