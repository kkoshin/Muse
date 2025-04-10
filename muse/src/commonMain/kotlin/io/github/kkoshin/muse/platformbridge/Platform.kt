package io.github.kkoshin.muse.platformbridge

enum class Platform {
    Android,
    Ios,
}

expect val CURRENT_PLATFORM: Platform