package io.github.kkoshin.muse.export

import android.net.Uri

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
        val uri: Uri,
    ) : ProgressStatus
}