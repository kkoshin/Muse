package io.github.kkoshin.muse.platformbridge

import okio.Path

actual fun Path.toNavRouteString(): String = toUri().toString()
