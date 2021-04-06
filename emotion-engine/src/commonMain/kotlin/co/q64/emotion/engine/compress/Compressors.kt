package co.q64.emotion.engine.compress

import co.q64.emotion.core.extension.nextOrNull
import co.q64.emotion.core.lang.compress.BaseCompressor
import co.q64.emotion.core.lang.compress.PairCompressor
import co.q64.emotion.core.lang.compress.RawCompressor
import co.q64.emotion.core.lang.compress.ShocoCompressor
import co.q64.emotion.core.lang.compress.SingleCompressor
import co.q64.emotion.core.opcode.OpcodeMarker
import co.q64.emotion.core.util.Codepage
import co.q64.emotion.engine.opcode.Opcodes

object Compressors {
    private val compressors = listOf(
        SingleCompressor,
        PairCompressor,
        ShocoCompressor,
        BaseCompressor,
        RawCompressor
    )

    fun compress(value: String): String = compressors
        .asSequence()
        .filter { it.canCompress(value) }
        .map { compressor ->
            val compressed = compressor.compress(value)
            val length = if (compressor.fixed != null) ByteArray(0) else encodeLength(compressed.size)
            compressor to length + compressed
        }
        .minByOrNull { (_, bytes) -> bytes.size }
        .let { it ?: error("Unable to compress string '$value'") }
        .let { (compressor, bytes) ->
            Opcodes.encode(Opcodes.find(compressor.marker)) + Codepage.encodeBytes(bytes)
        }

    fun decompress(marker: OpcodeMarker, iterator: Iterator<Codepage>): String {
        val compressor =
            compressors.firstOrNull { it.marker == marker } ?: error("Illegal compressor in decompression $marker")
        val length = compressor.fixed ?: decodeLength(iterator)
        val bytes = Codepage.decodeBytes(iterator.asSequence().take(length).toList())
        return compressor.decompress(bytes)
    }

    internal fun encodeLength(length: Int) =
        (0 until length / 255)
            .map { 255.toByte() }
            .plus((length % 255).toByte())
            .toByteArray()

    internal fun decodeLength(iterator: Iterator<Codepage>) =
        generateSequence(iterator.nextOrNull()) { previous ->
            when (previous) {
                Codepage.xff -> iterator.nextOrNull()
                else -> null
            }
        }
            .map { it.id }
            .sum()
}