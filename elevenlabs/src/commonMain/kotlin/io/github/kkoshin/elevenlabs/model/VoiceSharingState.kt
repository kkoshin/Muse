package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * export const VoiceSharingState = {
 *     Enabled: "enabled",
 *     Disabled: "disabled",
 *     Copied: "copied",
 *     CopiedDisabled: "copied_disabled",
 * } as const;
 */

@Serializable
enum class VoiceSharingState {
    @SerialName("enabled")
    Enabled,

    @SerialName("disabled")
    Disabled,

    @SerialName("copied")
    Copied,

    @SerialName("copied_disabled")
    CopiedDisabled,
}
