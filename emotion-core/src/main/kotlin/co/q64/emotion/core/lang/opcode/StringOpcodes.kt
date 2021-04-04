package co.q64.emotion.core.lang.opcode

import co.q64.emotion.core.opcode.OpcodeContainer
import co.q64.emotion.core.opcode.OpcodeRegistry
import co.q64.emotion.core.value.list
import co.q64.emotion.core.value.num
import co.q64.emotion.core.value.or
import co.q64.emotion.core.value.str

object StringOpcodes : OpcodeRegistry {
    override fun OpcodeContainer.register() {
        "str.concat"(
            "Push $a concatenated with $b.",
            str, str or num, push = str
        )
        { push(peek(2).string + pop(2).string) }
        "str.toUpperCase"(
            "Push $a converted to UPPER CASE.",
            str, push = str
        ) { push(pop().string.toUpperCase()) }
        "str.toLowerCase"(
            "Push $a converted to lower case.",
            str, push = str
        ) { push(pop().string.toLowerCase()) }
        "str.toCharList"(
            "Push the list of characters in $a.",
            str, push = list
        ) { push(pop().string.toCharArray().toList()) }
    }
}