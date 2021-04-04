package co.q64.emotion.core.lang.opcode

import co.q64.emotion.core.opcode.OpcodeContainer
import co.q64.emotion.core.opcode.OpcodeRegistry
import co.q64.emotion.core.value.any
import co.q64.emotion.core.value.num
import co.q64.emotion.core.value.str

object TestOpcodes : OpcodeRegistry {
    override fun OpcodeContainer.register() {
        "test.str-str"(str, str) {}
        "test.num-num"(num, num) {}
        "test.num-num2"(num, num) {}
        "test.str-num"(str, num) {}
        "test.str-any"(str, any) {}
        "test.str-any2"(str, any) {}
        "test.num-any"(num, any) {}
        "test.any-str"(any, str) {}
        "test.any-num"(any, num) {}
        "test.any-any"(any, num) {}
        "test.any-num-num"(any, num, num) {}
        "test.num-str"(num, str) {}
    }
}