package co.q64.emotion.core.lang.compress

import co.q64.emotion.core.compress.Compressor
import co.q64.emotion.core.opcode.Compress

object ShocoCompressor : Compressor {
    override val marker get() = Compress.Shoco
    override fun canCompress(value: String): Boolean = false

    override fun compress(value: String): ByteArray = TODO()
    //Shoco.INSTANCE.compress(value)

    override fun decompress(bytes: ByteArray): String = TODO()
    //Compress.Shoco.INSTANCE.decompress(bytes)
}