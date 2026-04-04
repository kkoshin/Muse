package io.github.kkoshin.muse.platformbridge

import okio.FileSystem

actual val CURRENT_PLATFORM: Platform = Platform.Desktop

actual val SystemFileSystem: FileSystem = FileSystem.SYSTEM
