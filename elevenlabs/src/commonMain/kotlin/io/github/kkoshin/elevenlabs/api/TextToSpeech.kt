package io.github.kkoshin.elevenlabs.api

import io.github.kkoshin.elevenlabs.ElevenLabsClient
import io.github.kkoshin.elevenlabs.bodyAsResult
import io.github.kkoshin.elevenlabs.model.OptimizeStreamingLatency
import io.github.kkoshin.elevenlabs.model.OutputFormat
import io.github.kkoshin.elevenlabs.model.TextToSpeechRequest
import io.ktor.resources.Resource
import java.io.InputStream

suspend fun ElevenLabsClient.textToSpeech(
    voiceId: String,
    textToSpeechRequest: TextToSpeechRequest,
    optimizeStreamingLatency: OptimizeStreamingLatency?,
    outputFormat: OutputFormat,
    logToHistory: Boolean = false,
): Result<InputStream> =
    post(
        TextToSpeech.VoiceId(
            voiceId = voiceId,
            enable_logging = logToHistory,
            optimize_streaming_latency = optimizeStreamingLatency?.value,
            output_format = outputFormat.value,
        ),
        data = textToSpeechRequest,
    ).bodyAsResult()

@Resource("/text-to-speech")
class TextToSpeech {
    /**
     * /text-to-speech/{voiceId}?enable_logging={enable_logging}&optimize_streaming_latency={optimize_streaming_latency}&output_format={output_format}
     */
    @Resource("{voiceId}")
    class VoiceId(
        val parent: TextToSpeech = TextToSpeech(),
        val voiceId: String,
        val enable_logging: Boolean? = null,
        val optimize_streaming_latency: String? = null,
        val output_format: String? = null,
    )
}