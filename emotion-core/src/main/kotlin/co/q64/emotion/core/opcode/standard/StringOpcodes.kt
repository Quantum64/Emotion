package co.q64.emotion.core.opcode.standard

import co.q64.emotion.core.opcode.OpcodeRegistry
import co.q64.emotion.core.value.string

object StringOpcodes : OpcodeRegistry {
    override fun register() {
        "string.toUpperCase"(string) { push(pop().string.toUpperCase()) }
        "string.toLowerCase"(string) { push(pop().string.toLowerCase()) }
        "string.toCharList"(string) { push(pop().string.toCharArray().toList()) }
    }
}