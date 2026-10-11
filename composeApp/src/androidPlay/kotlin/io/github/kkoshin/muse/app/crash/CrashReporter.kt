package io.github.kkoshin.muse.app.crash

import android.app.Application

/**
 * Google Play 渠道不采集崩溃日志：xcrash 的预编译 .so 非 16 KB 对齐，会被 Play 拒收。
 * Play 版的崩溃报告改由 Play Console 的 Android vitals 提供。
 */
internal fun installCrashReporter(@Suppress("UNUSED_PARAMETER") app: Application) = Unit
