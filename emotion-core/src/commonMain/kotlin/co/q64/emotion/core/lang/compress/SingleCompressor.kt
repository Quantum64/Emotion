package co.q64.emotion.core.lang.compress

import co.q64.emotion.core.compress.Compressor
import co.q64.emotion.core.opcode.Compress

object SingleCompressor : Compressor {
    override val marker get() = Compress.Single
    override val fixed get() = 1
    override fun canCompress(value: String): Boolean = value.encodeToByteArray().size == 1
    override fun compress(value: String): ByteArray = value.encodeToByteArray()
    override fun decompress(bytes: ByteArray): String = bytes.decodeToString()
}