package co.q64.emotion.core.opcode.standard

import co.q64.emotion.core.opcode.OpcodeRegistry

object BasicOpcodes : OpcodeRegistry {
    override fun register() {
        "load 0" { push(0) }
        "load 1" { push(1) }
        "load 2" { push(2) }
    }
}