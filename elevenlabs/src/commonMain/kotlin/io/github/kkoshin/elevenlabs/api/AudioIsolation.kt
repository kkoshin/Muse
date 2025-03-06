import io.github.kkoshin.elevenlabs.ElevenLabsClient
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.resources.Resource
import okio.Source
import okio.buffer

@Resource("/audio-isolation")
class AudioIsolation

suspend fun ElevenLabsClient.removeBackgroundAudio(
    audio: Source,
    audioName: String,
): Result<ByteArray> = postForm(AudioIsolation()) {
    append(
        "audio",
        audio.buffer().readByteArray(),
        headers = Headers.build {
            append(
                HttpHeaders.ContentDisposition,
                "form-data; name=\"audio\"; filename=\"$audioName\""
            )
            append(HttpHeaders.ContentType, ContentType.Audio.Any.toString())
        }
    )
}