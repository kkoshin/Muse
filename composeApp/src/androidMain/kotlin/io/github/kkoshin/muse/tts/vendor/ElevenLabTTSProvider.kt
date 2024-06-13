package io.github.kkoshin.muse.tts.vendor

import io.github.kkoshin.muse.audio.MonoAudioSampleMetadata
import io.github.kkoshin.muse.tts.SupportedAudioType
import io.github.kkoshin.muse.tts.TTSProvider
import io.github.kkoshin.muse.tts.TTSResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.andrewcpu.elevenlabs.ElevenLabs
import net.andrewcpu.elevenlabs.builders.SpeechGenerationBuilder
import net.andrewcpu.elevenlabs.enums.ElevenLabsVoiceModel
import net.andrewcpu.elevenlabs.enums.GeneratedAudioOutputFormat

class ElevenLabTTSProvider : TTSProvider {
    // Old Male with British accent
    private val presetBrian = "nPczCjzI2devNBz1zQrb"

    private val preferredVoiceId = presetBrian

    init {
        // 备用：7904879831bf1d4fd56f4f6baee9167b
        ElevenLabs.setApiKey("d41ee34b857479772db5ce143549bcd9")
//        GlobalScope.launch(Dispatchers.IO) {
//            debugLog {
//                val voices: List<Voice> = Voice.getVoices()
//                voices.map { it.name to it.voiceId }.toString()
//            }
//        }
    }

    /**
     * Output format of the generated audio.
     * Must be one of:
     *  - mp3_22050_32 - output format, mp3 with 22.05kHz sample rate at 32kbps.
     *  - mp3_44100_32 - output format, mp3 with 44.1kHz sample rate at 32kbps.
     *  - mp3_44100_64 - output format, mp3 with 44.1kHz sample rate at 64kbps.
     *  - mp3_44100_96 - output format, mp3 with 44.1kHz sample rate at 96kbps.
     *  - mp3_44100_128 - default output format, mp3 with 44.1kHz sample rate at 128kbps.
     *  Requires you to be subscribed to Creator tier or above.
     *  - mp3_44100_192 - output format, mp3 with 44.1kHz sample rate at 192kbps.
     *  - pcm_16000 - PCM format (S16LE) with 16kHz sample rate.
     *  - pcm_22050 - PCM format (S16LE) with 22.05kHz sample rate.
     *  - pcm_24000 - PCM format (S16LE) with 24kHz sample rate.
     *  - pcm_44100 - PCM format (S16LE) with 44.1kHz sample rate.
     *  Requires you to be subscribed to Pro tier or above.
     *  - ulaw_8000 - μ-law format (sometimes written mu-law, often approximated as u-law) with 8kHz sample rate.
     *  Note that this format is commonly used for Twilio audio inputs.
     */
    override suspend fun generate(text: String): Result<TTSResult> {
        check(text.isNotBlank()) {
            "text must not be blank."
        }
        return withContext(Dispatchers.IO) {
            runCatching {
                val generation = SpeechGenerationBuilder.textToSpeech()
                    .streamed()
                    .setText(text)
                    .setGeneratedAudioOutputFormat(GeneratedAudioOutputFormat.MP3_44100_128)
                    .setVoiceId(preferredVoiceId)
                    .setModel(ElevenLabsVoiceModel.ELEVEN_TURBO_V2)
                    .build()
                // 目前免费账号，所支持的音频格式是单通道的16bit的44.1khz
                TTSResult(generation, SupportedAudioType.MP3, MonoAudioSampleMetadata())
            }
        }
    }
}