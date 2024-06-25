package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * export type SubscriptionStatus =
 *     | "trialing"
 *     | "active"
 *     | "incomplete"
 *     | "incomplete_expired"
 *     | "past_due"
 *     | "canceled"
 *     | "unpaid"
 *     | "free";
 */
@Serializable
enum class SubscriptionStatus {
    @SerialName("trialing")
    Trialing,

    @SerialName("active")
    Active,

    @SerialName("incomplete")
    Incomplete,

    @SerialName("incomplete_expired")
    IncompleteExpired,

    @SerialName("past_due")
    PastDue,

    @SerialName("canceled")
    Canceled,

    @SerialName("unpaid")
    Unpaid,

    @SerialName("free")
    Free,
}