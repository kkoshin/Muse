package io.github.kkoshin.muse.platformbridge

import android.net.Uri
import okio.Path

fun Path.toUri(): Uri {
    val str = this.toString()

    if (str.startsWith("content:/")) {
        return Uri.parse(str.replace("content:/", "content://"))
    }

    return Uri.parse(str)
}