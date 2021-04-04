package co.q64.emotion.core.lang.opcode

import co.q64.emotion.core.opcode.OpcodeContainer
import co.q64.emotion.core.opcode.OpcodeRegistry
import co.q64.emotion.core.value.any

object BasicOpcodes : OpcodeRegistry {
    override fun OpcodeContainer.register() {
        "load 0" { push(0) }
        "load 1" { push(1) }
        "load 2" { push(2) }

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
            any, any
        ) { push(peek()) }

    }
}