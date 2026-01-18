package io.github.kkoshin.muse.platformbridge

expect inline fun logcat(tag: String = "Muse", block: () -> String)