package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * export interface Subscription {
 *     tier: string;
 *     character_count: number;
 *     character_limit: number;
 *     can_extend_character_limit: boolean;
 *     allowed_to_extend_character_limit: boolean;
 *     next_character_count_reset_unix: number;
 *     voice_limit: number;
 *     max_voice_add_edits?: number;
 *     voice_add_edit_counter?: number;
 *     professional_voice_limit: number;
 *     can_extend_voice_limit: boolean;
 *     can_use_instant_voice_cloning: boolean;
 *     can_use_professional_voice_cloning: boolean;
 *     currency?: ElevenLabs.Currency;
 *     status?: ElevenLabs.SubscriptionStatus;
 *     billing_period?: ElevenLabs.ExtendedSubscriptionResponseModelBillingPeriod;
 *     character_refresh_period?: ElevenLabs.ExtendedSubscriptionResponseModelCharacterRefreshPeriod;
 *     next_invoice?: ElevenLabs.Invoice;
 *     has_open_invoices?: boolean;
 * }
 */
@Serializable
data class Subscription(
    @SerialName("tier")
    val tier: String,
    @SerialName("character_count")
    val characterCount: Int,
    @SerialName("character_limit")
    val characterLimit: Int,
    @SerialName("can_extend_character_limit")
    val canExtendCharacterLimit: Boolean,
    @SerialName("allowed_to_extend_character_limit")
    val allowedToExtendCharacterLimit: Boolean,
    @SerialName("next_character_count_reset_unix")
    val nextCharacterCountResetUnix: Long,
    @SerialName("voice_limit")
    val voiceLimit: Int,
    @SerialName("max_voice_add_edits")
    val maxVoiceAddEdits: Int? = null,
    @SerialName("voice_add_edit_counter")
    val voiceAddEditCounter: Int? = null,
    @SerialName("professional_voice_limit")
    val professionalVoiceLimit: Int,
    @SerialName("can_extend_voice_limit")
    val canExtendVoiceLimit: Boolean,
    @SerialName("can_use_instant_voice_cloning")
    val canUseInstantVoiceCloning: Boolean,
    @SerialName("can_use_professional_voice_cloning")
    val canUseProfessionalVoiceCloning: Boolean,
    @SerialName("currency")
    val currency: Currency? = null,
    @SerialName("status")
    val status: SubscriptionStatus? = null,
    @SerialName("billing_period")
    val billingPeriod: ExtendedSubscriptionResponseModelBillingPeriod? = null,
    @SerialName("character_refresh_period")
    val characterRefreshPeriod: ExtendedSubscriptionResponseModelCharacterRefreshPeriod? = null,
    @SerialName("next_invoice")
    val nextInvoice: Invoice? = null,
    @SerialName("has_open_invoices")
    val hasOpenInvoices: Boolean? = null,
)
