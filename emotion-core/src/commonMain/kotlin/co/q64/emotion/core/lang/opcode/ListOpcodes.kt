package co.q64.emotion.core.lang.opcode

import co.q64.emotion.core.opcode.OpcodeContainer
import co.q64.emotion.core.opcode.OpcodeRegistry
import co.q64.emotion.core.value.any
import co.q64.emotion.core.value.list
import co.q64.emotion.core.value.num
import co.q64.emotion.core.value.str

object ListOpcodes : OpcodeRegistry {
    override fun OpcodeContainer.register() {
        "list.parseCsv"(
            "Push $a split on the delimiter \",\".",
            str, push = list
        ) { push(pop().string.split(",")) }
        "list.range"(
            "Push the range of integers from $a to $b (exclusive).",
            num, num, push = list
        ) { push((peek(2).int until pop(2).int).toList()) }
        "list.rangeInclusive"(
            "Push the range of integers from $a to $b (inclusive).",
            num, num, push = list
        ) { push((peek(2).int..pop(2).int).toList()) }
        "list.rangeTo"(
            "Push the range of integers from 0 to $a (exclusive).",
            num, push = list
        ) { push((0 until pop().int).toList()) }
        "list.rangeToInclusive"(
            "Push the range of integers from 0 to $a (inclusive).",
            num, push = list
        ) { push((0..pop().int).toList()) }
        "list.concat"(
            "Push a list of the elements of $a concatenated with the elements of $b.",
            list, list, push = list
        ) { push(peek(2).list + pop(2).list) }
        "list.add"(
            "Push a list of the elements of $a concatenated with $b.",
            list, any, push = list
        ) { push(peek(2).list + pop(2)) }
        "list.removeAt"(
            "Push $a with the element at index $b excluded.",
            list, num, push = list
        ) { push(peek(2).list.toMutableList().apply { removeAt(pop(2).int) }) }
        "list.first"(
            "Push the first element of $a.",
            list, push = any
        ) { push(pop().list.firstOrNull()) }
        "list.dropFirst"(
            "Push $a with the first $b elements excluded.",
            list, num, push = list
        ) { push(peek(2).list.drop(pop(2).int)) }
        "list.dropOneFirst"(
            "Push $a with the first element excluded.",
            list, push = list
        ) { push(pop().list.drop(1)) }
        "list.last"(
            "Push the last element of $a.",
            list, push = any
        ) { push(pop().list.lastOrNull()) }
        "list.dropLast"(
            "Push $a with the last $b elements excluded.",
            list, num, push = list
        ) { push(peek(2).list.dropLast(pop(2).int)) }
        "list.dropOneLast"(
            "Push $a with the last element excluded.",
            list, push = list
        ) { push(pop().list.dropLast(1)) }
        "list.max"(
            "Push the maximum element in $a.",
            list, push = any
        ) { push(pop().list.maxByOrNull { it.number }) }
        "list.min"(
            "Push the minimum element in $a.",
            list, push = any
        ) { push(pop().list.minByOrNull { it.number }) }
    }
}