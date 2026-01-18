package io.github.kkoshin.muse.feature.export

import okio.Path

sealed interface ProgressStatus {
    data object Idle : ProgressStatus

    open class Failed(
        val errorMsg: String,
        val throwable: Throwable?,
    ) : ProgressStatus

    open class Processing(
        val description: String,
    ) : ProgressStatus

    class Success(
        val path: Path,
    ) : ProgressStatus
}