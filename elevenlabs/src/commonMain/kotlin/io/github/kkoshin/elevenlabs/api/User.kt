package io.github.kkoshin.elevenlabs.api

import io.github.kkoshin.elevenlabs.ElevenLabsClient
import io.github.kkoshin.elevenlabs.bodyAsResult
import io.github.kkoshin.elevenlabs.model.Subscription
import io.ktor.resources.Resource

suspend fun ElevenLabsClient.getSubscription(): Result<Subscription> = get(User.Subscription()).bodyAsResult()

@Resource("/user")
class User {
    @Resource("/subscription")
    class Subscription(
        val parent: User = User(),
    )
}