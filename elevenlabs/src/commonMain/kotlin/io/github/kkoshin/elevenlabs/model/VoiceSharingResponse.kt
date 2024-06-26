package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * export interface VoiceSharingResponse {
 *     status?: ElevenLabs.VoiceSharingState;
 *     history_item_sample_id?: string;
 *     date_unix?: number;
 *     whitelisted_emails?: string[];
 *     public_owner_id?: string;
 *     original_voice_id?: string;
 *     financial_rewards_enabled?: boolean;
 *     free_users_allowed?: boolean;
 *     live_moderation_enabled?: boolean;
 *     rate?: number;
 *     notice_period?: number;
 *     disable_at_unix?: number;
 *     voice_mixing_allowed?: boolean;
 *     featured?: boolean;
 *     category?: ElevenLabs.Category;
 *     reader_app_enabled?: boolean;
 *     ban_reason?: string;
 *     liked_by_count?: number;
 *     cloned_by_count?: number;
 *     name?: string;
 *     description?: string;
 *     labels?: Record<string, string>;
 *     review_status?: ElevenLabs.ReviewStatus;
 *     review_message?: string;
 *     enabled_in_library?: boolean;
 *     instagram_username?: string;
 *     twitter_username?: string;
 *     youtube_username?: string;
 *     tiktok_username?: string;
 * }
 */
@Serializable
data class VoiceSharingResponse(
    @SerialName("status")
    val status: VoiceSharingState?,
    @SerialName("history_item_sample_id")
    val historyItemSampleId: String?,
    @SerialName("date_unix")
    val dateUnix: Long?,
    @SerialName("whitelisted_emails")
    val whitelistedEmails: List<String>?,
    @SerialName("public_owner_id")
    val publicOwnerId: String?,
    @SerialName("original_voice_id")
    val originalVoiceId: String?,
    @SerialName("financial_rewards_enabled")
    val financialRewardsEnabled: Boolean,
    @SerialName("free_users_allowed")
    val freeUsersAllowed: Boolean,
    @SerialName("live_moderation_enabled")
    val liveModerationEnabled: Boolean,
    @SerialName("rate")
    val rate: Double?,
    @SerialName("notice_period")
    val noticePeriod: Int?,
    @SerialName("disable_at_unix")
    val disableAtUnix: Long?,
    @SerialName("voice_mixing_allowed")
    val voiceMixingAllowed: Boolean?,
    @SerialName("featured")
    val featured: Boolean?,
    @SerialName("category")
    val category: Category?,
    @SerialName("reader_app_enabled")
    val readerAppEnabled: Boolean?,
    @SerialName("ban_reason")
    val banReason: String?,
    @SerialName("liked_by_count")
    val likedByCount: Int?,
    @SerialName("cloned_by_count")
    val clonedByCount: Int?,
    @SerialName("name")
    val name: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("labels")
    val labels: Map<String, String>,
    @SerialName("review_status")
    val reviewStatus: ReviewStatus?,
    @SerialName("review_message")
    val reviewMessage: String?,
    @SerialName("enabled_in_library")
    val enabledInLibrary: Boolean?,
)
