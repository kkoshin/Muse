package io.github.kkoshin.muse.audio

import okio.BufferedSource
import okio.ByteString.Companion.encodeUtf8
import okio.IOException

class WavParser(
    private val source: BufferedSource
) : AutoCloseable {
    // 使用字符串直接量提高可读性
    private val RIFF_HEADER = "RIFF".encodeUtf8()
    private val WAVE_FORMAT = "WAVE".encodeUtf8()
    private val FMT_CHUNK = "fmt ".encodeUtf8()
    private val DATA_CHUNK = "data".encodeUtf8()

    var sampleRate = 0
        private set
    var channels = 0
        private set
    var sampleBits = 0
        private set
    private var fileSize = 0L
    var dataSize = 0L
        private set

    init {
        parseHeader()
    }

    @Throws(IOException::class)
    private fun parseHeader() {
        // 1. 读取 RIFF 头
        val riffHeader = source.readByteString(4)
        require(riffHeader == RIFF_HEADER) {
            "Invalid WAVE header: ${riffHeader.utf8()}"
        }

        // 2. 文件大小 (小端序)
        fileSize = source.readIntLe().toLong() and 0xFFFFFFFFL

        // 3. WAVE 格式标识
        val waveFormat = source.readByteString(4)
        require(waveFormat == WAVE_FORMAT) {
            "Invalid WAVE format: ${waveFormat.utf8()}"
        }

        // 4. fmt 子块
        val fmtChunk = source.readByteString(4)
        require(fmtChunk == FMT_CHUNK) {
            "Invalid fmt chunk: ${fmtChunk.utf8()}"
        }

        // 5. fmt 子块大小 (小端序)
        val fmtSize = source.readIntLe().toLong()
        require(fmtSize >= 16) { "Invalid fmt size: $fmtSize" }

        // 6. 音频格式 (PCM=1)
        val audioFormat = source.readShortLe().toInt()
        require(audioFormat == 1) { "Unsupported audio format: $audioFormat" }

        // 7. 通道数 (小端序)
        channels = source.readShortLe().toInt() and 0xFFFF

        // 8. 采样率 (小端序)
        sampleRate = source.readIntLe()

        // 9. 字节率 (跳过)
        source.skip(4)

        // 10. 块对齐 (跳过)
        source.skip(2)

        // 11. 样本位数
        sampleBits = source.readShortLe().toInt() and 0xFFFF

        // 12. 跳过可能存在的扩展区域
        if (fmtSize > 16) {
            source.skip(fmtSize - 16)
        }

        // 13. 定位 data 子块
        findDataChunk()
    }

    @Throws(IOException::class)
    private fun findDataChunk() {
        while (true) {
            val chunkId = source.readByteString(4)
            dataSize = source.readIntLe().toLong() and 0xFFFFFFFFL

            if (chunkId == DATA_CHUNK) {
                return
            }
            // 跳过其他类型的子块
            source.skip(dataSize)
        }
    }

    fun getFileSize(): Long = fileSize + 8 // 包含 RIFF 头大小

    fun getLengthSeconds(): Double {
        return if (sampleRate == 0 || channels == 0 || sampleBits == 0) 0.0 else {
            dataSize.toDouble() / (sampleRate * channels * (sampleBits / 8.0))
        }
    }

    @Throws(IOException::class)
    fun readMono(dst: ShortArray, numSamples: Int): Int {
        require(channels == 1) { "Not a mono file" }
        return readSamples(dst, numSamples, channels = 1)
    }

    @Throws(IOException::class)
    fun readStereo(left: ShortArray, right: ShortArray, numSamples: Int): Int {
        require(channels == 2) { "Not a stereo file" }

        // 创建临时缓冲存储交织的左右声道数据
        val tempBuffer = ShortArray(numSamples * 2)
        val samplesRead = readSamples(tempBuffer, numSamples, channels = 2)

        // 解交织左右声道
        for (i in 0 until samplesRead) {
            left[i] = tempBuffer[i * 2]
            right[i] = tempBuffer[i * 2 + 1]
        }
        return samplesRead
    }

    // 修改后的 readSamples 实现：
    @Throws(IOException::class)
    private fun readSamples(
        dst: ShortArray,
        numSamples: Int,
        channels: Int
    ): Int {
        val bytesPerSample = sampleBits / 8
        val totalBytes = numSamples * channels * bytesPerSample
        val byteBuffer = ByteArray(totalBytes.coerceAtMost(8192))

        var samplesRead = 0
        while (samplesRead < numSamples && !source.exhausted()) {
            val bytesNeeded = (numSamples - samplesRead) * channels * bytesPerSample
            val bytesToRead = bytesNeeded.coerceAtMost(byteBuffer.size)
            val bytesRead = source.read(byteBuffer, 0, bytesToRead)

            if (bytesRead <= 0) break

            // 计算本次读取的样本数
            val samplesInChunk = bytesRead / (channels * bytesPerSample)

            when (bytesPerSample) {
                2 -> decodeSamples16bit(dst, samplesRead, byteBuffer, bytesRead, channels)
                1 -> decodeSamples8bit(dst, samplesRead, byteBuffer, bytesRead, channels)
                else -> throw IOException("Unsupported sample size: $bytesPerSample")
            }

            samplesRead += samplesInChunk
        }
        return samplesRead
    }

    // 更新解码方法以包含起始位置
    private fun decodeSamples16bit(
        dst: ShortArray,
        startSample: Int,
        buffer: ByteArray,
        bytesRead: Int,
        channels: Int
    ) {
        val samples = bytesRead / 2
        val startIndex = startSample * channels

        for (i in 0 until samples) {
            val offset = i * 2
            val lo = buffer[offset].toInt() and 0xFF
            val hi = buffer[offset + 1].toInt() and 0xFF shl 8
            dst[startIndex + i] = (hi or lo).toShort()
        }
    }

    private fun decodeSamples8bit(
        dst: ShortArray,
        startSample: Int,
        buffer: ByteArray,
        bytesRead: Int,
        channels: Int
    ) {
        val startIndex = startSample * channels

        for (i in 0 until bytesRead) {
            dst[startIndex + i] = ((buffer[i].toInt() and 0xFF) - 128).shl(8).toShort()
        }
    }

    override fun close() {
        source.close()
    }
}