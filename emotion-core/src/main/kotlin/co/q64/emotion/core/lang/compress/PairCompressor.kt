package co.q64.emotion.core.lang.compress

import co.q64.emotion.core.compress.Compressor
import co.q64.emotion.core.opcode.Compress

object PairCompressor : Compressor {
    override val marker get() = Compress.Pair
    override val fixed get() = 2
    override fun canCompress(value: String): Boolean = value.encodeToByteArray().size == 2
    override fun compress(value: String): ByteArray = value.encodeToByteArray()
    override fun decompress(bytes: ByteArray): String = String(bytes)
}