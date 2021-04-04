package co.q64.emotion.engine.compiler.lower

import co.q64.emotion.core.lang.Instruction
import co.q64.emotion.core.opcode.Control
import co.q64.emotion.engine.compiler.CompiledInstruction
import co.q64.emotion.engine.compiler.Lowering
import co.q64.emotion.engine.opcode.Opcodes

object CompressAdjacentEndLowering : Lowering {
    private val end = Instruction.fixed(Opcodes.find(Control.End))

    internal fun compress(count: Int) =
        (0 until count / 3)
            .map { Control.Internal.End3 }
            .let { first ->
                count.mod(3).takeIf { it > 0 }
                    ?.let { result ->
                        first + when (result) {
                            1 -> Control.End
                            2 -> Control.Internal.End2
                            else -> error("Math is broken")
                        }
                    } ?: first
            }
            .map { Opcodes.find(it) }

    override fun lower(instructions: List<CompiledInstruction>): List<CompiledInstruction> =
        instructions.fold(emptyList<CompiledInstruction>() to 0) { (result, buffer), instruction ->
            when (instruction.opcode?.marker) {
                Control.End -> result to buffer + 1
                else -> result + compress(buffer).map { generated ->
                    CompiledInstruction.create(generated, (result.lastOrNull()?.line ?: 2) - 1)
                } + instruction to 0
            }
        }
            .let { (result, _) -> result } // Ignore last end instructions

    override fun raise(instructions: List<Instruction>): List<Instruction> = instructions
        .flatMap {
            when (it.marker) {
                Control.Internal.End2 -> (0..1).map { end }
                Control.Internal.End3 -> (0..2).map { end }
                else -> listOf(it)
            }
        }
}