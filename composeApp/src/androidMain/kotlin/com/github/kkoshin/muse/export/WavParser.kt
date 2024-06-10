package com.github.kkoshin.muse.export

import java.io.BufferedInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

class WavParser(
    private val fileStream: InputStream
) {
    private val WAV_HEADER_CHUNK_ID: Int = 0x52494646 // "RIFF"
    private val WAV_FORMAT: Int = 0x57415645 // "WAVE"
    private val WAV_FORMAT_CHUNK_ID: Int = 0x666d7420 // "fmt "
    private val WAV_DATA_CHUNK_ID: Int = 0x64617461 // "data"
    private val STREAM_BUFFER_SIZE: Int = 4096

    private var mInStream: BufferedInputStream? = null

    private var mSampleRate = 0
    private var mChannels = 0
    private var mSampleBits = 0
    private var mFileSize = 0
    private var mDataSize = 0

    /**
     * Open WAV file for reading
     *
     * @throws FileNotFoundException if input file does not exist
     * @throws IllegalStateException if input file is not a valid WAVE file
     * @throws IOException if I/O error occurred during file read
     */
    @Throws(FileNotFoundException::class, IllegalStateException::class, IOException::class)
    fun openWave() {
        mInStream = BufferedInputStream(fileStream, STREAM_BUFFER_SIZE)

        val headerId = readUnsignedInt(mInStream!!) // should be "RIFF"
        if (headerId != WAV_HEADER_CHUNK_ID) {
            throw IllegalStateException(String.format("Invalid WAVE header chunk ID: %d", headerId))
        }
        mFileSize = readUnsignedIntLE(mInStream!!) // length of header
        val format = readUnsignedInt(mInStream!!) // should be "WAVE"
        if (format != WAV_FORMAT) {
            throw IllegalStateException("Invalid WAVE format")
        }

        val formatId = readUnsignedInt(mInStream!!) // should be "fmt "
        if (formatId != WAV_FORMAT_CHUNK_ID) {
            throw IllegalStateException("Invalid WAVE format chunk ID")
        }
        val formatSize = readUnsignedIntLE(mInStream!!)
        if (formatSize != 16) {
        }
        val audioFormat = readUnsignedShortLE(mInStream!!).toInt()
        if (audioFormat != 1) {
            throw IllegalStateException("Not PCM WAVE format")
        }
        mChannels = readUnsignedShortLE(mInStream!!).toInt()
        mSampleRate = readUnsignedIntLE(mInStream!!)
        val byteRate = readUnsignedIntLE(mInStream!!)
        val blockAlign = readUnsignedShortLE(mInStream!!).toInt()
        mSampleBits = readUnsignedShortLE(mInStream!!).toInt()

        val dataId = readUnsignedInt(mInStream!!)
        if (dataId != WAV_DATA_CHUNK_ID) {
            throw IllegalStateException("Invalid WAVE data chunk ID: $dataId")
        }
        mDataSize = readUnsignedIntLE(mInStream!!)
    }

    /**
     * Get sample rate
     *
     * @return input file's sample rate
     */
    fun getSampleRate(): Int {
        return mSampleRate
    }

    /**
     * Get number of channels
     *
     * @return number of channels in input file
     */
    fun getChannels(): Int {
        return mChannels
    }

    /**
     * Get PCM format, S16LE or S8LE
     *
     * @return number of bits per sample
     */
    fun getPcmFormat(): Int {
        return mSampleBits
    }

    /**
     * Get file size
     *
     * @return total input file size in bytes
     */
    fun getFileSize(): Int {
        return mFileSize + 8
    }

    /**
     * Get input file's audio data size
     * Basically file size without headers included
     *
     * @return audio data size in bytes
     */
    fun getDataSize(): Int {
        return mDataSize
    }

    /**
     * Get input file length
     *
     * @return length of file in seconds
     */
    fun getLength(): Int {
        return if ((mSampleRate == 0 || mChannels == 0) || (mSampleBits + 7) / 8 == 0) {
            0
        } else {
            mDataSize / (mSampleRate * mChannels * ((mSampleBits + 7) / 8))
        }
    }

    /**
     * Read audio data from input file (mono)
     *
     * @param dst  mono audio data output buffer
     * @param numSamples  number of samples to read
     *
     * @return number of samples read
     *
     * @throws IOException if file I/O error occurs
     */
    @Throws(IOException::class)
    fun read(dst: ShortArray, numSamples: Int): Int {
        if (mChannels != 1) {
            return -1
        }

        val buf = ByteArray(numSamples * 2)
        var index = 0
        val bytesRead = mInStream!!.read(buf, 0, numSamples * 2)

        var i = 0
        while (i < bytesRead) {
            dst[index] = byteToShortLE(buf[i], buf[i + 1])
            index++
            i += 2
        }

        return index
    }

    /**
     * Read audio data from input file (stereo)
     *
     * @param left  left channel audio output buffer
     * @param right  right channel audio output buffer
     * @param numSamples  number of samples to read
     *
     * @return number of samples read
     *
     * @throws IOException if file I/O error occurs
     */
    @Throws(IOException::class)
    fun read(left: ShortArray, right: ShortArray, numSamples: Int): Int {
        if (mChannels != 2) {
            return -1
        }
        val buf = ByteArray(numSamples * 4)
        var index = 0
        val bytesRead = mInStream!!.read(buf, 0, numSamples * 4)

        var i = 0
        while (i < bytesRead) {
            val `val` = byteToShortLE(buf[0], buf[i + 1])
            if (i % 4 == 0) {
                left[index] = `val`
            } else {
                right[index] = `val`
                index++
            }
            i += 2
        }

        return index
    }

    /**
     * Close WAV file. WaveReader object cannot be used again following this call.
     *
     * @throws IOException if I/O error occurred closing filestream
     */
    @Throws(IOException::class)
    fun closeWaveFile() {
        if (mInStream != null) {
            mInStream!!.close()
        }
    }

    private fun byteToShortLE(b1: Byte, b2: Byte): Short {
        return (b1.toInt() and 0xFF or ((b2.toInt() and 0xFF) shl 8)).toShort()
    }

    @Throws(IOException::class)
    private fun readUnsignedInt(`in`: BufferedInputStream): Int {
        val ret: Int
        val buf = ByteArray(4)
        ret = `in`.read(buf)
        return if (ret == -1) {
            -1
        } else {
            (((buf[0].toInt() and 0xFF) shl 24)
                    or ((buf[1].toInt() and 0xFF) shl 16)
                    or ((buf[2].toInt() and 0xFF) shl 8)
                    or (buf[3].toInt() and 0xFF))
        }
    }

    @Throws(IOException::class)
    private fun readUnsignedIntLE(`in`: BufferedInputStream): Int {
        val ret: Int
        val buf = ByteArray(4)
        ret = `in`.read(buf)
        return if (ret == -1) {
            -1
        } else {
            (buf[0].toInt() and 0xFF or ((buf[1].toInt() and 0xFF) shl 8)
                    or ((buf[2].toInt() and 0xFF) shl 16)
                    or ((buf[3].toInt() and 0xFF) shl 24))
        }
    }

    @Throws(IOException::class)
    private fun readUnsignedShortLE(`in`: BufferedInputStream): Short {
        val ret: Int
        val buf = ByteArray(2)
        ret = `in`.read(buf, 0, 2)
        return if (ret == -1) {
            -1
        } else {
            byteToShortLE(buf[0], buf[1])
        }
    }
}