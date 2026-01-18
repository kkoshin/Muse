package io.github.kkoshin.muse.platformbridge

import okio.Path
import okio.Sink

/**
 * 分享音频文件
 */
expect fun shareAudioFile(path: Path): Result<Unit>

expect fun openFile(path: Path): Result<Unit>

/**
 * 返回一个临时文件的路径
 */
expect fun createCacheFile(fileName: String, sensitive: Boolean): Path

/**
 * 处理了 Android Uri 的情况
 */
expect fun Path.toSink(): Sink