package io.github.kkoshin.muse.feature.export

import kotlinx.coroutines.flow.StateFlow
import okio.BufferedSink

interface ExportStatus {
    // 0..100
    val progress: StateFlow<Int>
}

interface ExportPipeline<T> : ExportStatus {
    /**
     * 导出文件到 target
     * 注意 outputSink 需在外部处理资源释放
     */
    suspend fun start(outputSink: BufferedSink): Result<T>

    fun cancel()
}