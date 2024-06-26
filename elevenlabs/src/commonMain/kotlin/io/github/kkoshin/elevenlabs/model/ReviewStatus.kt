package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * export const ReviewStatus = {
 *     NotRequested: "not_requested",
 *     Pending: "pending",
 *     Declined: "declined",
 *     Allowed: "allowed",
 *     AllowedWithChanges: "allowed_with_changes",
 * } as const;
 */
@Serializable
enum class ReviewStatus {
    @SerialName("not_requested")
    NotRequested,

    @SerialName("pending")
    Pending,

    @SerialName("declined")
    Declined,

    @SerialName("allowed")
    Allowed,

    @SerialName("allowed_with_changes")
    AllowedWithChanges,
}
