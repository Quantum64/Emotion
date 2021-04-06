package co.q64.emotion.core.lang.compress

import co.q64.emotion.core.compress.Compressor
import co.q64.emotion.core.opcode.Compress
import co.q64.emotion.core.opcode.OpcodeMarker
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign

object BaseCompressor : Compressor {
    override val marker: OpcodeMarker get() = Compress.Base
    override fun canCompress(value: String): Boolean = value.all { it.isDigit() }

    override fun compress(value: String): ByteArray = BigInteger.parseString(value).toByteArray()
    override fun decompress(bytes: ByteArray): String = BigInteger.fromByteArray(bytes, Sign.POSITIVE).toString()
}