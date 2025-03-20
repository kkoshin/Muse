@file:OptIn(ExperimentalUuidApi::class)

package io.github.kkoshin.muse.repo.model

import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class Script(
    val id: Uuid = Uuid.random(),
    val title: String = "Untitled",
    val text: String,
    val createAt: Long = Clock.System.now().toEpochMilliseconds(),
) {
    val summary: String = text.take(100).replace("\n", " ")
}