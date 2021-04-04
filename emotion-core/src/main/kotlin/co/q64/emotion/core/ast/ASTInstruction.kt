package co.q64.emotion.core.ast

import co.q64.emotion.core.lang.Instruction
import co.q64.emotion.core.lang.Program

class ASTInstruction(
    private val program: Program,
    private val instruction: Instruction
) : ASTNode {
    override fun enter(): ASTResult {
        if (!program.terminated()) {
            try {
                instruction.execute(program)
            } catch (e: Exception) {
                program.crash("${e::class.java.simpleName}: ${e.message} [Instruction: $instruction]")
            }
        }
        return ASTResult.None
    }

    override fun toString() = "$instruction"
}