package io.github.kkoshin.sixtydb.api

import io.github.kkoshin.sixtydb.SixtyDbClient
import io.github.kkoshin.sixtydb.model.ChatCompletionRequest
import io.github.kkoshin.sixtydb.model.ChatCompletionResponse
import io.github.kkoshin.sixtydb.model.ChatMessage
import io.ktor.resources.Resource

/**
 * `POST /v1/chat/completions` — OpenAI-compatible. 60db's default LLM is
 * `60db-tiny`. Exposed for future Muse features (e.g. AI scriptwriting); not
 * yet wired into a processor.
 */
@Resource("/v1/chat/completions")
class ChatCompletions

suspend fun SixtyDbClient.chatCompletion(
    messages: List<ChatMessage>,
    model: String = "60db-tiny",
    temperature: Double = 0.7,
    maxTokens: Int? = null,
): Result<String> {
    val body = ChatCompletionRequest(
        model = model,
        messages = messages,
        stream = false,
        temperature = temperature,
        maxTokens = maxTokens,
    )
    return post<ChatCompletionRequest, ChatCompletions, ChatCompletionResponse>(
        ChatCompletions(),
        data = body,
    ).map { resp -> resp.choices.firstOrNull()?.message?.content.orEmpty() }
}
