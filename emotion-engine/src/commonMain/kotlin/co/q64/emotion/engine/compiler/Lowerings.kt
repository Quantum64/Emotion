package co.q64.emotion.engine.compiler

import co.q64.emotion.core.lang.Instruction
import co.q64.emotion.engine.compiler.lower.CompressAdjacentEndLowering

object Lowerings {
    private val all = listOf(
        CompressAdjacentEndLowering
    )

    fun lower(instructions: List<CompiledInstruction>): List<CompiledInstruction> = all
        .fold(instructions) { result, lowering -> lowering.lower(result) }

    fun raise(instructions: List<Instruction>): List<Instruction> = all
        .fold(instructions) { result, lowering -> lowering.raise(result) }
}