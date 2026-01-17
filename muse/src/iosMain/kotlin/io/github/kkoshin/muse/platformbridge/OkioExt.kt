package io.github.kkoshin.muse.platformbridge

import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSURL

internal fun NSURL.toOkioPath(): Path? {
    return this.path?.toPath()
}

internal fun Path?.toNsUrl(): NSURL? {
    return this?.let {
        val str = it.toString()
        if (str.startsWith("/")) {
            NSURL.fileURLWithPath(str)
        } else {
            // Check for URL schemes and ensure they are followed by ://
            val schemeMatch = Regex("^([a-z]+):/(?![/])").find(str)
            if (schemeMatch != null) {
                val scheme = schemeMatch.groupValues[1]
                NSURL.URLWithString(str.replace("$scheme:/", "$scheme://"))
            } else {
                NSURL.URLWithString(str)
            }
        }
    }
}
