package io.github.kkoshin.muse.platformbridge

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

actual fun Long.formatTimeDisplay(): String = Instant.ofEpochMilli(this).let {
    val time = LocalDateTime.ofInstant(it, ZoneId.systemDefault())
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(time)
}