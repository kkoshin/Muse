package io.github.kkoshin.muse.dashboard

import java.util.UUID

data class Script(
    val id: UUID = UUID.randomUUID(),
    val title: String = "Untitled",
    val text: String,
) {
    val summary: String = text.take(100).replace("\n", " ")
}