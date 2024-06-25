package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * export interface Invoice {
 *     amount_due_cents: number;
 *     next_payment_attempt_unix: number;
 * }
 */
@Serializable
data class Invoice(
    @SerialName("amount_due_cents")
    val amountDueCents: Int,
    @SerialName("next_payment_attempt_unix")
    val nextPaymentAttemptUnix: Long,
)
