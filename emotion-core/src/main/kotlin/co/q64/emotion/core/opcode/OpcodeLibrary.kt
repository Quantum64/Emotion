package co.q64.emotion.core.opcode

import co.q64.emotion.core.opcode.standard.BasicOpcodes
import co.q64.emotion.core.opcode.standard.NumberOpcodes
import co.q64.emotion.core.opcode.standard.StringOpcodes
import co.q64.emotion.core.opcode.standard.TestOpcodes

object OpcodeLibrary {
    val opcodes = mutableListOf<Opcode>()

    init {
        sequenceOf(
            BasicOpcodes,
            NumberOpcodes,
            StringOpcodes,
            TestOpcodes
        )
            .forEach { it.register() }
    }
}