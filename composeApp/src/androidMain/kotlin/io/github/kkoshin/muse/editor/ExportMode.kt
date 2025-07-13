package io.github.kkoshin.muse.editor

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ExportMode {
    // 朗读模式
    @SerialName("reading")
    Reading,
    // 听写模式
    @SerialName("dictation")
    Dictation,
}