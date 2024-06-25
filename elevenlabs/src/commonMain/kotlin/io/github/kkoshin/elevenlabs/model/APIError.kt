package io.github.kkoshin.elevenlabs.model

import kotlinx.serialization.Serializable

/**
 * {"detail":{"status":"invalid_api_key","message":"Invalid API key"}}
 */
@Serializable
data class APIError(
    val detail: Detail,
) {
    @Serializable
    data class Detail(
        val status: String,
        val message: String,
    )
}