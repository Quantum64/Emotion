package co.q64.emotion.core.opcode

import co.q64.emotion.core.lang.Program
import co.q64.emotion.core.value.ValueType

interface OpcodeRegistry {
    fun OpcodeContainer.register()
}