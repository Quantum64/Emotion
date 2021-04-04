package co.q64.emotion.core.lang.compress

import co.q64.emotion.core.compress.Compressor
import co.q64.emotion.core.compress.Shoco
import co.q64.emotion.core.opcode.Compress

object ShocoCompressor : Compressor {
    override val marker get() = Compress.Shoco

    override fun compress(value: String): ByteArray =
        Shoco.INSTANCE.compress(value)

    override fun decompress(bytes: ByteArray): String =
        Shoco.INSTANCE.decompress(bytes)
}