package co.q64.emotion.core.lang

import co.q64.emotion.core.opcode.OpcodeMarker
import co.q64.emotion.core.value.Value

sealed interface Instruction {
    val marker: OpcodeMarker? get() = null
    fun execute(program: Program)

    data class Execute(
        private val pending: PendingInstruction
    ) : Instruction {
        override val marker: OpcodeMarker? get() = pending.marker

        override fun execute(program: Program) = with(pending.resolve(program)) {
            program.closure()
        }

        override fun toString(): String = "$pending"
    }

    data class Load(
        private val value: Value
    ) : Instruction {
        override fun execute(program: Program) {
            program.push(value)
        }

        override fun toString(): String = "load $value"
    }

    object NoOp : Instruction {
        override fun execute(program: Program) = Unit
        override fun toString(): String = "no-op"
    }
}