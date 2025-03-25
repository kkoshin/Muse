package io.github.kkoshin.muse.platformbridge

import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSURL

internal fun NSURL.toOkioPath(): Path? {
    return this.absoluteString?.toPath()
}

internal fun Path?.toNsUrl(): NSURL? {
    return this?.let {
        NSURL.URLWithString(it.toString())
    }
}
