package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * export interface Voice {
 *     voice_id: string;
 *     name?: string;
 *     samples?: ElevenLabs.VoiceSample[];
 *     category?: string;
 *     fine_tuning?: ElevenLabs.FineTuningResponse;
 *     labels?: Record<string, string>;
 *     description?: string;
 *     preview_url?: string;
 *     available_for_tiers?: string[];
 *     settings?: ElevenLabs.VoiceSettings;
 *     sharing?: ElevenLabs.VoiceSharingResponse;
 *     high_quality_base_model_ids?: string[];
 *     safety_control?: ElevenLabs.VoiceResponseModelSafetyControl;
 *     voice_verification?: ElevenLabs.VoiceVerificationResponse;
 *     owner_id?: string;
 *     permission_on_resource?: string;
 * }
 */
@Serializable
data class Voice(
    @SerialName("voice_id")
    val voiceId: String,
    @SerialName("name")
    val name: String,
    @SerialName("samples")
    val samples: List<VoiceSample>? = null,
    @SerialName("category")
    val category: String? = null,
    @SerialName("fine_tuning")
    val fineTuning: FineTuningResponse?,
    @SerialName("labels")
    val labels: Map<String, String>?,
    @SerialName("description")
    val description: String?,
    @SerialName("preview_url")
    val previewUrl: String,
    @SerialName("available_for_tiers")
    val availableForTiers: List<String>? = null,
    @SerialName("settings")
    val settings: VoiceSettings? = null,
    @SerialName("sharing")
    val sharing: VoiceSharingResponse? = null,
    @SerialName("high_quality_base_model_ids")
    val highQualityBaseModelIds: List<String>? = null,
    @SerialName("safety_control")
    val safetyControl: VoiceResponseModelSafetyControl? = null,
    @SerialName("voice_verification")
    val voiceVerification: VoiceVerificationResponse? = null,
    @SerialName("owner_id")
    val ownerId: String? = null,
    @SerialName("permission_on_resource")
    val permissionOnResource: String? = null,
)

@Serializable
class VoicesResponse(
    val voices: List<Voice>,
)