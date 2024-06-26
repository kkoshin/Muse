package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * export interface FineTuningResponse {
 *     is_allowed_to_fine_tune?: boolean;
 *     finetuning_state?: ElevenLabs.FinetuningState;
 *     verification_failures?: string[];
 *     verification_attempts_count?: number;
 *     manual_verification_requested?: boolean;
 *     language?: string;
 *     finetuning_progress?: Record<string, number>;
 *     message?: string;
 *     dataset_duration_seconds?: number;
 *     verification_attempts?: ElevenLabs.VerificationAttemptResponse[];
 *     slice_ids?: string[];
 *     manual_verification?: ElevenLabs.ManualVerificationResponse;
 * }
 */
@Serializable
data class FineTuningResponse(
    @SerialName("is_allowed_to_fine_tune")
    val isAllowedToFineTune: Boolean? = null,
    @SerialName("finetuning_state")
    val finetuningState: FinetuningState? = null,
    @SerialName("verification_failures")
    val verificationFailures: List<String>? = null,
    @SerialName("verification_attempts_count")
    val verificationAttemptsCount: Int? = null,
    @SerialName("manual_verification_requested")
    val manualVerificationRequested: Boolean? = null,
    val language: String? = null,
    @SerialName("finetuning_progress")
    val finetuningProgress: Map<String, Double>? = null,
    val message: String? = null,
    @SerialName("dataset_duration_seconds")
    val datasetDurationSeconds: Int? = null,
    @SerialName("verification_attempts")
    val verificationAttempts: List<VerificationAttemptResponse>? = null,
    @SerialName("slice_ids")
    val sliceIds: List<String>? = null,
    @SerialName("manual_verification")
    val manualVerification: ManualVerificationResponse? = null,
)
