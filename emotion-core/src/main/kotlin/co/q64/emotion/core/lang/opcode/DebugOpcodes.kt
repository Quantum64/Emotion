package co.q64.emotion.core.lang.opcode

import co.q64.emotion.core.lang.value.TypeValue
import co.q64.emotion.core.opcode.OpcodeContainer
import co.q64.emotion.core.opcode.OpcodeRegistry
import co.q64.emotion.core.value.any
import co.q64.emotion.core.value.children

object DebugOpcodes : OpcodeRegistry {
    override fun OpcodeContainer.register() {
        "debug.asserttypes"(
            "Compiler generated type assertion."
        ) {
            (0 until pop().int)
                .map { pop() as TypeValue }
                .withIndex()
                .forEach { (index, expected) ->
                    if (expected.value != any && peek(index + 1).type !in expected.value.children) {
                        crash("Type assertion failed: Expected ${expected.value.name} but found ${peek(index + 1).type.name}")
                    }
                }
        }
    }
}