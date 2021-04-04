package co.q64.emotion.core.opcode

import co.q64.emotion.core.lang.Program
import co.q64.emotion.core.value.ValueType

data class OpcodeContainer(
    val opcodes: MutableList<Opcode> = mutableListOf()
) {
    operator fun String.invoke(
        vararg values: ValueType,
        closure: Program.() -> Unit
    ) = invoke(description = "No description available.", values = *values, closure = closure)

    operator fun String.invoke(
        description: String,
        vararg values: ValueType,
        closure: Program.() -> Unit
    ) = invoke(description, null, *values, closure = closure)

    operator fun String.invoke(
        description: String = "No description available.",
        marker: OpcodeMarker? = null,
        vararg values: ValueType,
        closure: Program.() -> Unit
    ) = opcodes.add(
        Opcode(
            id = this,
            values = values.toList(),
            marker = marker,
            description = description,
            closure = closure
        )
    )

    val a get() = "`a`"
    val b get() = "`b`"
    val c get() = "`c`"
    val d get() = "`d`"
    val e get() = "`e`"
    val f get() = "`f`"
    val g get() = "`g`"
}
