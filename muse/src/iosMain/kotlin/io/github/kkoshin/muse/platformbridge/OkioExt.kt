package io.github.kkoshin.muse.platformbridge

import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSURL

internal fun NSURL.toOkioPath(): Path? {
    return this.absoluteString?.toPath()
}

internal fun Path?.toNsUrl(): NSURL? {
    return this?.let {
        val str = it.toString()
        if (str.startsWith("https:/") && !str.startsWith("https://")) {
            NSURL.URLWithString(str.replace("https:/", "https://"))
        } else {
            NSURL.URLWithString(str)
        }
    }
}
