package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * export const FinetuningState = {
 *     NotStarted: "not_started",
 *     Queued: "queued",
 *     FineTuning: "fine_tuning",
 *     FineTuned: "fine_tuned",
 *     Failed: "failed",
 *     Delayed: "delayed",
 * } as const;
 */
@Serializable
enum class FinetuningState {
    @SerialName("not_started")
    NotStarted,

    @SerialName("queued")
    Queued,

    @SerialName("fine_tuning")
    FineTuning,

    @SerialName("fine_tuned")
    FineTuned,

    @SerialName("failed")
    Failed,

    @SerialName("delayed")
    Delayed,
}
