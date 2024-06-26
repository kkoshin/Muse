package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * export interface VoiceVerificationResponse {
 *     requires_verification: boolean;
 *     is_verified: boolean;
 *     verification_failures: string[];
 *     verification_attempts_count: number;
 *     language?: string;
 *     verification_attempts?: ElevenLabs.VerificationAttemptResponse[];
 * }
 */
@Serializable
class VoiceVerificationResponse(
    @SerialName("requires_verification")
    val requiresVerification: Boolean,
    @SerialName("is_verified")
    val isVerified: Boolean,
    @SerialName("verification_failures")
    val verificationFailures: List<String>,
    @SerialName("verification_attempts_count")
    val verificationAttemptsCount: Int,
    @SerialName("language")
    val language: String? = null,
    @SerialName("verification_attempts")
    val verificationAttempts: List<VerificationAttemptResponse>? = null,
)