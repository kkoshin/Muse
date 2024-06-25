package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName

/**
 * export type ExtendedSubscriptionResponseModelBillingPeriod = "monthly_period" | "annual_period";
 */
enum class ExtendedSubscriptionResponseModelBillingPeriod {
    @SerialName("monthly_period")
    MonthlyPeriod,

    @SerialName("annual_period")
    AnnualPeriod,
}