package co.q64.emotion.core.lang.opcode

import co.q64.emotion.core.opcode.Compress
import co.q64.emotion.core.opcode.Control
import co.q64.emotion.core.opcode.OpcodeContainer
import co.q64.emotion.core.opcode.OpcodeRegistry

object InternalOpcodes : OpcodeRegistry {
    override fun OpcodeContainer.register() {
        "internal.compress.single"(marker = Compress.Single)
        "internal.compress.pair"(marker = Compress.Pair)
        "internal.compress.shoco"(marker = Compress.Shoco)
        "internal.compress.base"(marker = Compress.Base)

        "internal.end.2"(marker = Control.Internal.End2)
        "internal.end.3"(marker = Control.Internal.End3)
    }
}