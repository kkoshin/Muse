package com.github.kkoshin.muse

import logcat.logcat

fun debugLog(action: () -> String) = logcat("debugLog") {
    action()
}