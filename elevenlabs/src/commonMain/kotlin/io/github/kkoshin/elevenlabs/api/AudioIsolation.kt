import io.github.kkoshin.elevenlabs.ElevenLabsClient
import io.ktor.http.ContentType
import io.ktor.resources.Resource
import okio.Source
import okio.buffer

@Resource("/audio-isolation")
class AudioIsolation

suspend fun ElevenLabsClient.removeBackgroundAudio(
    audio: Source,
    audioName: String,
): Result<ByteArray> = postAsForm(
    resources = AudioIsolation(),
    audioFile = audio.buffer().readByteArray(),
    fileName = audioName,
    contentType = ContentType.Audio.MPEG
)
