package co.q64.emotion.core.opcode

import co.q64.emotion.core.lang.Stack
import co.q64.emotion.core.value.ValueType

interface OpcodeRegistry {
    fun register()

    operator fun String.invoke(
        vararg values: ValueType,
        marker: OpcodeMarker? = null,
        description: String = "No description available.",
        closure: Stack.() -> Unit
    ) = OpcodeLibrary.opcodes.add(
        Opcode(
            id = this,
            values = values.toList(),
            marker = marker,
            description = description,
            closure = closure
        )
    )
}