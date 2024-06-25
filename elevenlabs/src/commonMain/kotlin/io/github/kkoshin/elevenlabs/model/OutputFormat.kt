package io.github.kkoshin.elevenlabs.model

enum class OutputFormat(
    val value: String,
) {
    Mp3_22050_32("mp3_22050_32"),

    Mp3_44100_32("mp3_44100_32"),

    Mp3_44100_64("mp3_44100_64"),

    Mp3_44100_96("mp3_44100_96"),

    Mp3_44100_128("mp3_44100_128"),

    // -------------------------------------

    Mp3_44100_192("mp3_44100_192"),

    Pcm_16000("pcm_16000"),

    Pcm_22050("pcm_22050"),

    Pcm_24000("pcm_24000"),

    Pcm_44100("pcm_44100"),

    Ulaw_8000("ulaw_8000"),
}

object FreeTierOutputFormat {
    val Mp3_22050_32 = OutputFormat.Mp3_22050_32
    val Mp3_44100_32 = OutputFormat.Mp3_44100_32
    val Mp3_44100_64 = OutputFormat.Mp3_44100_64
    val Mp3_44100_96 = OutputFormat.Mp3_44100_96
    val Mp3_44100_128 = OutputFormat.Mp3_44100_128
}
