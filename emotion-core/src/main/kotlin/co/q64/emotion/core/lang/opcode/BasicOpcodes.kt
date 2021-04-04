package co.q64.emotion.core.lang.opcode

import co.q64.emotion.core.opcode.Control
import co.q64.emotion.core.opcode.OpcodeContainer
import co.q64.emotion.core.opcode.OpcodeRegistry
import co.q64.emotion.core.value.any
import co.q64.emotion.core.value.num

object BasicOpcodes : OpcodeRegistry {
    override fun OpcodeContainer.register() {
        for (value in 0..10) {
            "load $value"(
                push = num
            ) { push(value) }
        }

        "end"(
            "End a control flow structure.",
            marker = Control.End
        )

        "stop"(
            "Stop the program."
        )
        { stop(print = false) }

        "stop.print"(
            "Print the top stack value, then stop the program."
        )
        { stop(print = true) }

        "any.dup"(
            "Push $a, $a.",
            any, any, push = any
        ) { push(peek()) }

    }
}