package io.github.kkoshin.muse.platformbridge

actual inline fun logcat(tag: String, block: () -> String) {
    println("$tag: ${block()}")
}
