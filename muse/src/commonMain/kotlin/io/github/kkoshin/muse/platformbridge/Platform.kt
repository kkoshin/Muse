package io.github.kkoshin.muse.platformbridge

import okio.FileSystem

enum class Platform {
    Android,
    Ios,
}

expect val CURRENT_PLATFORM: Platform

// FIXME: 是否有替代的写法
expect val SystemFileSystem: FileSystem