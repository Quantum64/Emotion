package co.q64.emotion.core.compress

import co.q64.emotion.core.opcode.OpcodeMarker

interface Compressor {
    val marker: OpcodeMarker
    val fixed: Int? get() = null
    fun canCompress(value: String) = true
    fun compress(value: String): ByteArray
    fun decompress(bytes: ByteArray): String
}