package io.github.kkoshin.muse.platformbridge

import okio.FileSystem

actual val CURRENT_PLATFORM: Platform
    get() = Platform.Android
actual val SystemFileSystem: FileSystem
    get() = FileSystem.SYSTEM