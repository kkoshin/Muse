package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * export const VoiceResponseModelSafetyControl = {
 *     None: "NONE",
 *     Ban: "BAN",
 *     Captcha: "CAPTCHA",
 *     CaptchaAndModeration: "CAPTCHA_AND_MODERATION",
 * } as const;
 */
@Serializable
enum class VoiceResponseModelSafetyControl {
    @SerialName("NONE")
    None,

    @SerialName("BAN")
    Ban,

    @SerialName("CAPTCHA")
    Captcha,

    @SerialName("CAPTCHA_AND_MODERATION")
    CaptchaAndModeration,
}
