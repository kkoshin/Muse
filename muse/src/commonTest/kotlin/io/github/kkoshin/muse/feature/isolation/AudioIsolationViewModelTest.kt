package io.github.kkoshin.muse.feature.isolation

import io.github.kkoshin.muse.core.manager.AudioIsolationProcessor
import io.github.kkoshin.muse.feature.export.ProgressStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okio.Path
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AudioIsolationViewModelTest {

    class FakeAudioIsolationProcessor(
        val result: Result<Path>
    ) : AudioIsolationProcessor {
        override suspend fun removeBackgroundNoiseAndSave(audioUri: Path): Result<Path> {
            return result
        }
    }

    @Test
    fun testRemoveBackgroundNoiseSuccess() = runTest {
        val expectedPath = "/path/to/denoised.mp3".toPath()
        val processor = FakeAudioIsolationProcessor(Result.success(expectedPath))
        val viewModel = AudioIsolationViewModel(processor)

        viewModel.removeBackgroundNoise("/input.mp3".toPath())

        val progress = viewModel.progress.first { it is ProgressStatus.Success }
        assertTrue(progress is ProgressStatus.Success)
        assertEquals(expectedPath, progress.path)
    }

    @Test
    fun testRemoveBackgroundNoiseFailure() = runTest {
        val errorMsg = "API Error"
        val processor = FakeAudioIsolationProcessor(Result.failure(Exception(errorMsg)))
        val viewModel = AudioIsolationViewModel(processor)

        viewModel.removeBackgroundNoise("/input.mp3".toPath())

        val progress = viewModel.progress.first { it is ProgressStatus.Failed }
        assertTrue(progress is ProgressStatus.Failed)
        assertEquals(errorMsg, progress.errorMsg)
    }
}
