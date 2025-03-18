package io.github.kkoshin.elevenlabs

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.cancel
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.runBlocking
import okio.Buffer
import okio.Source
import okio.Timeout

class StreamingByteReadChannelSource(
    private val channel: ByteReadChannel,
    private val bufferSize: Int = DEFAULT_BUFFER_SIZE
) : Source {
    private val closed = atomic(false)
    private val internalBuffer = ByteArray(bufferSize)

    override fun read(sink: Buffer, byteCount: Long): Long {
        check(!closed.value) { "StreamingByteReadChannelSource is closed" }
        require(byteCount >= 0) { "byteCount < 0: $byteCount" }

        return runBlocking(Dispatchers.IO) {
            val readSize = byteCount.coerceAtMost(bufferSize.toLong()).toInt()
            val bytesRead = channel.readAvailable(internalBuffer, 0, readSize)

            when {
                bytesRead == -1 -> -1L
                bytesRead > 0 -> {
                    sink.write(internalBuffer, 0, bytesRead)
                    bytesRead.toLong()
                }
                else -> 0L
            }
        }
    }

    override fun timeout(): Timeout = Timeout.NONE

    override fun close() {
        if (closed.compareAndSet(false, true)) {
            channel.cancel()
        }
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 8192
    }
}