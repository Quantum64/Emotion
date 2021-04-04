package co.q64.emotion.core.ast

import co.q64.emotion.core.lang.Program
import co.q64.emotion.core.opcode.Compare
import co.q64.emotion.core.opcode.OpcodeMarker
import co.q64.emotion.core.value.Value

data class ASTCondition(
    private val program: Program,
    private val type: OpcodeMarker,
    private val pass: ASTNode,
    private val fail: ASTNode
) : ASTNode {
    override fun enter(): ASTResult {
        val other = when (type) {
            Compare.True -> Value.True
            Compare.False -> Value.False
            else -> program.pop()
        }
        val value = program.pop()
        return when (type) {
            Compare.Greater -> value > other
            Compare.GreaterEqual -> value >= other
            Compare.Less -> value < other
            Compare.LessEqual -> value <= other
            Compare.NotEqual -> value != other
            else -> value == other
        }
            .takeIf { it }
            ?.let { pass.enter() } ?: fail.enter()
    }

    override fun toString() = "{if $type $pass else $fail}"
}