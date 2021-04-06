package co.q64.emotion.core.ast

import co.q64.emotion.core.lang.Program
import co.q64.emotion.core.opcode.Iterate
import co.q64.emotion.core.opcode.OpcodeMarker
import co.q64.emotion.core.value.value

data class ASTIterator(
    private val program: Program,
    private val body: ASTNode,
    private val type: OpcodeMarker
) : ASTNode {
    override fun enter(): ASTResult {
        for ((index, value) in program.pop().list.asSequence().withIndex()) {
            program.memory.apply {
                i = index.value()
                o = value
            }
            if (type == Iterate.Push) program.push(value)
            if (program.terminated()) break
            when (body.enter()) {
                ASTResult.Break -> break
                ASTResult.Continue -> continue
                else -> Unit
            }
        }
        return ASTResult.None
    }

    override fun toString(): String = "(iterate $body)"
}