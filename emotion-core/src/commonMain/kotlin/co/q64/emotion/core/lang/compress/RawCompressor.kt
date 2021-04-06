package co.q64.emotion.core.lang.compress

import co.q64.emotion.core.compress.Compressor
import co.q64.emotion.core.opcode.Compress

object RawCompressor : Compressor {
    override val marker get() = Compress.Raw
    override fun compress(value: String): ByteArray = value.encodeToByteArray()
    override fun decompress(bytes: ByteArray): String = bytes.decodeToString()
}