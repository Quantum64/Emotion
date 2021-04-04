package co.q64.emotion.core.lang

import co.q64.emotion.core.Debug
import co.q64.emotion.core.opcode.Opcode
import co.q64.emotion.core.value.any
import co.q64.emotion.core.value.children

data class PendingInstruction(
    private val options: List<Opcode>
) {
    val marker = options.firstOrNull()?.marker
    val empty get() = options.isEmpty()

    fun resolve(stack: Stack) = options
        .asSequence()
        .filter { opcode ->
            opcode.values
                .asReversed()
                .asSequence()
                .zip(
                    generateSequence(1 to stack.peek()) { (next, _) ->
                        next + 1 to stack.peek(next)
                    }
                        .map { (_, value) -> value }
                        .map { it.type }
                )
                .all { (target, actual) ->
                    target == any || actual in target.children
                }
        }
        .let { sequence ->
            if (Debug.UnambiguousOpcodeAssertion) {
                val list = sequence.toList()
                if (list.size > 1) {
                    error("Ambiguous opcode [${list.joinToString()}]")
                }
                list.asSequence()
            } else sequence
        }
        .firstOrNull()
        ?: error("Illegal opcode stack state [${options.joinToString()}]. (Try enabling type assertions)")

    override fun toString() = when {
        options.isEmpty() -> "nop"
        else -> options.joinToString("|") { it.id }
    }
}