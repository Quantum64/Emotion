package co.q64.emotion.core.opcode

import co.q64.emotion.core.lang.opcode.BasicOpcodes
import co.q64.emotion.core.lang.opcode.InternalOpcodes
import co.q64.emotion.core.lang.opcode.ListOpcodes
import co.q64.emotion.core.lang.opcode.NumberOpcodes
import co.q64.emotion.core.lang.opcode.StringOpcodes
import co.q64.emotion.core.lang.opcode.TestOpcodes

object OpcodeLibrary {
    val opcodes: List<Opcode> = sequenceOf(
        InternalOpcodes,
        BasicOpcodes,
        NumberOpcodes,
        StringOpcodes,
        ListOpcodes,
        TestOpcodes
    )
        .map { opcode -> opcode.run { OpcodeContainer().apply { register() } } }
        .flatMap { container -> container.opcodes }
        .toList()

    val names = opcodes.associateBy { it.id }
    val markers = opcodes.associateBy { it.marker }
}