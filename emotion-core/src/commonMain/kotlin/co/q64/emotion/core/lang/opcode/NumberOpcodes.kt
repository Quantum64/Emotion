package co.q64.emotion.core.lang.opcode

import co.q64.emotion.core.math.rational
import co.q64.emotion.core.opcode.OpcodeContainer
import co.q64.emotion.core.opcode.OpcodeRegistry
import co.q64.emotion.core.value.list
import co.q64.emotion.core.value.num
import co.q64.emotion.core.value.or
import co.q64.emotion.core.value.str
import kotlin.math.sign

object NumberOpcodes : OpcodeRegistry {
    override fun OpcodeContainer.register() {
        "num.concat"(
            "Push $a concatenated with $b as a string.",
            num, num or str, push = str
        )
        { push(peek(2).string + pop(2).string) }
        "num.toString"(
            "Push a string representation of $a.",
            num, push = str
        ) { push(pop().string) }
        "num.increment"(
            "Push the sum of $a and 1.",
            num, push = num
        ) { push(pop().number.add(1.rational())) }
        "num.decrement"(
            "Push the difference of $a and 1.",
            num, push = num
        ) { push(pop().number.subtract(1.rational())) }
        "num.add"(
            "Push the sum of $a and $b.",
            num, num, push = num
        ) { push(pop().number.add(pop().number)) }
        "num.subtract"(
            "Push the difference of $a and $b.",
            num, num, push = num
        ) { push(peek(2).number.subtract(pop(2).number)) }
        "num.multiply"(
            "Push the product of $a and $b.",
            num, num, push = num
        ) { push(pop().number.multiply(pop().number)) }
        "num.divide"(
            "Push the quotient of $a and $b.",
            num, num, push = num
        ) { push(peek(2).number.divide(pop(2).number)) }
        "num.modulus"(
            "Push the modulus of $a and $b.",
            num, num, push = num
        ) { push(peek(2).long % pop(2).long) }
        "num.mod2"(
            "Push the modulus of $a and 2.",
            num, num, push = num
        ) { push(pop().long % 2) }
        "num.sign"(
            "Push 1 if $a is positive, push -1 if $a is negative, else push 0.",
            num, push = num
        ) { push(pop().long.sign) }
        "num.flipSign"(
            "Push the product of $a and -1.",
            num, push = num
        ) { push(pop().number.multiply((-1).rational())) }
        "num.digits"(
            "Push a list of the digits of $a.",
            num, push = list
        ) {
            push(pop().string.filter { it.isDigit() }.map { "$it".toInt() })
        }
    }
}