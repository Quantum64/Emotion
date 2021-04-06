package co.q64.emotion.engine.compiler

import co.q64.emotion.core.lang.Instruction

interface Lowering {
    fun lower(instructions: List<CompiledInstruction>): List<CompiledInstruction>
    fun raise(instructions: List<Instruction>): List<Instruction>
}