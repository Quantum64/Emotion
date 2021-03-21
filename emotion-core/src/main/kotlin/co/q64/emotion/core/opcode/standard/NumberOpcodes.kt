package co.q64.emotion.core.opcode.standard

import co.q64.emotion.core.math.rational
import co.q64.emotion.core.opcode.OpcodeRegistry
import co.q64.emotion.core.value.number

object NumberOpcodes : OpcodeRegistry {
    override fun register() {
        "number.increment"(number) { push(pop().number.add(1.rational())) }
        "number.decrement"(number) { push(pop().number.subtract(1.rational())) }
        "number.multiply"(number, number) { push(pop().number.multiply(pop().number)) }
    }
}