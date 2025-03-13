package io.github.kkoshin.muse.feature.export

import kotlinx.coroutines.flow.StateFlow
import okio.Path

interface ExportStatus {
    // 0..100
    val progress: StateFlow<Int>
}

interface ExportPipeline<T> : ExportStatus {
    /**
     * 导出文件到 target
     */
    suspend fun start(target: Path): Result<T>

    fun cancel()
}