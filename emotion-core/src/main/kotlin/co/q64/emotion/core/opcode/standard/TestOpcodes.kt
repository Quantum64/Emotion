package co.q64.emotion.core.opcode.standard

import co.q64.emotion.core.opcode.OpcodeRegistry
import co.q64.emotion.core.value.any
import co.q64.emotion.core.value.number
import co.q64.emotion.core.value.string

object TestOpcodes : OpcodeRegistry {
    override fun register() {
        "test.str-str"(string, string) {}
        "test.num-num"(number, number) {}
        "test.num-num2"(number, number) {}
        "test.str-num"(string, number) {}
        "test.str-any"(string, any) {}
        "test.str-any2"(string, any) {}
        "test.num-any"(number, any) {}
        "test.any-str"(any, string) {}
        "test.any-num"(any, number) {}
        "test.any-any"(any, number) {}
        "test.any-num-num"(any, number, number) {}
        "test.num-str"(number, string) {}
    }
}