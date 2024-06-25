package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.Serializable

/**
 * export interface ValidationError {
 *     loc: ElevenLabs.ValidationErrorLocItem[];
 *     msg: string;
 *     type: string;
 * }
 */
@Serializable
data class ValidationError(
    // TODO: 不确定是否能正常解析，暂时先注释掉
//    val loc: List<ValidationErrorLocItem>,
    val msg: String,
    val type: String,
)

@Serializable
sealed class ValidationErrorLocItem

@Serializable
data class StringItem(val value: String) : ValidationErrorLocItem()
@Serializable
data class IntItem(val value: Int) : ValidationErrorLocItem()
