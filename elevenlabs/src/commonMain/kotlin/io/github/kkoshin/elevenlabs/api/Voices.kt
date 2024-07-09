package io.github.kkoshin.elevenlabs.api

import io.github.kkoshin.elevenlabs.ElevenLabsClient
import io.github.kkoshin.elevenlabs.model.Voice
import io.github.kkoshin.elevenlabs.model.VoicesResponse
import io.ktor.resources.Resource

@Resource("/voices")
class Voices

suspend fun ElevenLabsClient.getVoices(): Result<List<Voice>> = get<Voices, VoicesResponse>(Voices()).map { it.voices }