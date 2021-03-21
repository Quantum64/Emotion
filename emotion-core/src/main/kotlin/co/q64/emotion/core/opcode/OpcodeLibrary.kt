package co.q64.emotion.core.opcode

import co.q64.emotion.core.opcode.standard.BasicOpcodes
import co.q64.emotion.core.opcode.standard.NumberOpcodes
import co.q64.emotion.core.opcode.standard.StringOpcodes

object OpcodeLibrary {
    val opcodes = mutableListOf<Opcode>()

    fun load() {
        sequenceOf(
            BasicOpcodes,
            NumberOpcodes,
            StringOpcodes
        )
            .forEach { it.register() }
    }
}