package io.github.kkoshin.muse.platformbridge

import platform.Foundation.NSLog

actual inline fun logcat(tag: String, block: () -> String) {
    NSLog("%@", block())
}