package co.q64.emotion.test.util

import co.q64.emotion.core.opcode.Opcode
import co.q64.emotion.engine.compiler.CompiledInstruction

fun createCompiled(vararg opcodes: Opcode) =
    opcodes.mapIndexed { index, opcode -> CompiledInstruction.create(opcode, index + 1) }