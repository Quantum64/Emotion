package co.q64.emotion.core.lang.opcode

import co.q64.emotion.core.opcode.OpcodeContainer
import co.q64.emotion.core.opcode.OpcodeRegistry
import co.q64.emotion.core.value.num
import co.q64.emotion.core.value.or
import co.q64.emotion.core.value.str

object StringOpcodes : OpcodeRegistry {
    override fun OpcodeContainer.register() {
        "str.concat"(
            "Push $a concatenated with $b.",
            str, str or num
        )
        { push(peek(2).string + pop(2).string) }
        "str.toUpperCase"(str) { push(pop().string.toUpperCase()) }
        "str.toLowerCase"(str) { push(pop().string.toLowerCase()) }
        "str.toCharList"(str) { push(pop().string.toCharArray().toList()) }
    }
}