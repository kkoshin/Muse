package io.github.kkoshin.muse.repo.model

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import kotlinx.datetime.Clock

data class Script(
    val id: Uuid = uuid4(),
    val title: String = "Untitled",
    val text: String,
    val createAt: Long = Clock.System.now().toEpochMilliseconds(),
) {
    val summary: String = text.take(100).replace("\n", " ")
}