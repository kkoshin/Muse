package io.github.kkoshin.muse.tts.vendor

import android.content.Context
import io.github.kkoshin.muse.audio.MonoAudioSampleMetadata
import io.github.kkoshin.muse.debugLog
import io.github.kkoshin.muse.tts.CharacterQuota
import io.github.kkoshin.muse.tts.SupportedAudioType
import io.github.kkoshin.muse.tts.TTSProvider
import io.github.kkoshin.muse.tts.TTSResult
import io.github.kkoshin.muse.tts.Voice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class MockTTSProvider : TTSProvider {
    private val appContext: Context by inject(Context::class.java)

    override suspend fun queryQuota(): Result<CharacterQuota> = Result.success(CharacterQuota(100, 100, null))

    override suspend fun queryVoices(): Result<List<Voice>> {
        delay(1000)
        debugLog {
            "queryVoices from mock"
        }
        return Result.success(
            listOf(
                Voice(
                    voiceId = "ThT5KcBeYPX3keUQqHPh1",
                    name = "Dorothy1",
                    description = "pleasant",
                    useCase = "children's stories",
                    accent = Voice.Accent.American,
                    age = Voice.Age.Young,
                    gender = Voice.Gender.Female,
                    descriptive = null,
                    previewUrl =
                    "https://storage.googleapis.com/eleven-public-prod/premade/voices/ThT5KcBeYPX3keUQqHPh/981f0855-6598-48d2-9f8f-b6d92fbbe3fc.mp3",
                ),
                Voice(
                    voiceId = "ThT5KcBeYPX3keUQqHPh2",
                    name = "Dorothy2",
                    description = "pleasant",
                    useCase = "children's stories",
                    accent = Voice.Accent.British,
                    age = Voice.Age.Young,
                    gender = Voice.Gender.Female,
                    descriptive = null,
                    previewUrl =
                    "https://storage.googleapis.com/eleven-public-prod/premade/voices/ThT5KcBeYPX3keUQqHPh/981f0855-6598-48d2-9f8f-b6d92fbbe3fc.mp3",
                ),
                Voice(
                    voiceId = "ThT5KcBeYPX3keUQqHPh3",
                    name = "Dorothy3",
                    description = "pleasant",
                    useCase = "children's stories",
                    accent = Voice.Accent.British,
                    age = Voice.Age.Young,
                    gender = Voice.Gender.Female,
                    descriptive = null,
                    previewUrl =
                    "https://storage.googleapis.com/eleven-public-prod/premade/voices/ThT5KcBeYPX3keUQqHPh/981f0855-6598-48d2-9f8f-b6d92fbbe3fc.mp3",
                ),
            ),
        )
    }

    override suspend fun generate(
        voiceId: String,
        text: String,
    ): Result<TTSResult> =
        runCatching {
            withContext(Dispatchers.IO) {
                delay(1000)
                TTSResult(
                    content = appContext.assets.open("english.mp3"),
                    mimeType = SupportedAudioType.MP3,
                    audioSampleMetadata = MonoAudioSampleMetadata(),
                )
            }
        }
}