package co.q64.emotion.engine.compiler

import co.q64.emotion.core.opcode.Opcode
import co.q64.emotion.engine.opcode.Opcodes

data class CompiledInstruction(
    val opcode: Opcode?,
    val instruction: String,
    val description: String,
    val encoded: String,
    val line: Int
) {
    companion object {
        fun create(opcode: Opcode, line: Int) = CompiledInstruction(
            opcode = opcode,
            instruction = opcode.id,
            description = opcode.description,
            encoded = Opcodes.encode(opcode),
            line = line
        )
    }
}