package io.github.kkoshin.muse.feature.editor

enum class ExportMode {
    // 朗读模式
    Reading,
    // 听写模式
    Dictation;

    companion object {
        fun fromName(name: String): ExportMode? {
            return entries.find { it.name == name }
        }
    }

}