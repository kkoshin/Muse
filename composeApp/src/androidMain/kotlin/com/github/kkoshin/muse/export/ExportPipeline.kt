package com.github.kkoshin.muse.export

import android.net.Uri
import kotlinx.coroutines.flow.StateFlow

interface ExportStatus {
    // 0..100
    val progress: StateFlow<Int>
}

interface ExportPipeline<T> : ExportStatus {
    suspend fun start(target: Uri): Result<T>

    fun cancel()
}