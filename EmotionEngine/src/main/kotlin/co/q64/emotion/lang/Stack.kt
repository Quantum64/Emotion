package co.q64.emotion.lang

import co.q64.emotion.lang.value.NullValue
import co.q64.emotion.lang.value.Value
import co.q64.emotion.lang.value.value
import java.util.Collections

class Stack {
    private val stack = mutableListOf<Value>()

    val size get() = stack.size

    fun pop(depth: Int = 1) = (1..depth).map { if (size > 0) stack.removeAt(0) else NullValue }.last()
    fun dup(times: Int = 1) = if (size > 0) (1..times).forEach { _ -> push(peek()) } else Unit
    fun peek(depth: Int = 1): Value = if (size >= depth) stack[size - depth] else NullValue
    fun swap() = if (size > 1) Collections.swap(stack, size - 1, size - 2) else Unit
    fun clear() = stack.clear()

    fun push(any: Any) {
        val value = any.value()
        // TODO something?
        stack.add(value)
    }


}