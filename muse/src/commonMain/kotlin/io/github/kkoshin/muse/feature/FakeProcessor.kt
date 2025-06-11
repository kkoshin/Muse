package io.github.kkoshin.muse.feature

import io.github.kkoshin.muse.audio.MonoAudioSampleMetadata
import io.github.kkoshin.muse.core.provider.CharacterQuota
import io.github.kkoshin.muse.core.provider.SupportedAudioType
import io.github.kkoshin.muse.core.provider.TTSProvider
import io.github.kkoshin.muse.core.provider.TTSResult
import io.github.kkoshin.muse.core.provider.Voice
import io.github.kkoshin.muse.platformbridge.logcat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import muse.feature.generated.resources.Res
import okio.Buffer
import okio.IOException
import okio.Source
import okio.Timeout

// 使用本地数据来测试验证流程
class FakeProcessor : TTSProvider {
    override suspend fun generate(
        voiceId: String,
        text: String
    ): Result<TTSResult> = runCatching {
        withContext(Dispatchers.IO) {
            delay(1000)
            val data = Res.readBytes("files/english_mock_data.mp3")
            TTSResult(
                content = createSource(data),
                mimeType = SupportedAudioType.MP3,
                audioSampleMetadata = MonoAudioSampleMetadata(),
            )
        }
    }

    override suspend fun queryQuota(): Result<CharacterQuota> =  Result.success(CharacterQuota(100, 100, null))

    override suspend fun queryVoices(): Result<List<Voice>> {
        delay(1000)
        logcat {
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
}

suspend fun createSource(readBytes: ByteArray): Source = withContext(Dispatchers.Default) {
    object : Source {
        private val buffer = Buffer()
        private val mutex = Mutex()
        private var closed = false
        private var readComplete = false

        init {
            // 启动协程异步加载数据
            CoroutineScope(Dispatchers.Unconfined).launch {
                try {
                    mutex.withLock {
                        buffer.write(readBytes)
                        readComplete = true
                    }
                } catch (e: Throwable) {
                    mutex.withLock {
                        closed = true
                    }
                }
            }
        }

        override fun read(sink: Buffer, byteCount: Long): Long {
            return runBlocking {
                mutex.withLock {
                    if (closed) throw IOException("Source closed")
                    while (!readComplete) {
                        // 等待数据加载完成
                        mutex.unlock()
                        delay(10) // 避免忙等待
                        mutex.lock()
                    }
                    if (buffer.size == 0L) -1 else buffer.read(sink, byteCount)
                }
            }
        }

        override fun timeout(): Timeout = Timeout.NONE

        override fun close() {
            runBlocking {
                mutex.withLock {
                    buffer.clear()
                    closed = true
                }
            }
        }
    }
}