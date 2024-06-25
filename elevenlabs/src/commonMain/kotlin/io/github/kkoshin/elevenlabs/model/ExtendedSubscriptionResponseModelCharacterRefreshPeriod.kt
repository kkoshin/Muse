package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * export type ExtendedSubscriptionResponseModelCharacterRefreshPeriod = "monthly_period" | "annual_period";
 */
@Serializable
enum class ExtendedSubscriptionResponseModelCharacterRefreshPeriod {
    @SerialName("monthly_period")
    MonthlyPeriod,

    @SerialName("annual_period")
    AnnualPeriod,
}