package co.q64.emotion.core.lang

import co.q64.emotion.core.lang.value.NullValue
import co.q64.emotion.core.value.Value
import co.q64.emotion.core.value.value

interface Stack {
    val size: Int
    fun pop(depth: Int = 1): Value
    fun dup(times: Int = 1)
    fun peek(depth: Int = 1): Value
    fun swap()
    fun clear()
    fun push(any: Any?)

    data class Simple(
        private val stack: MutableList<Value> = mutableListOf()
    ) : Stack {
        override val size get() = stack.size
        override fun pop(depth: Int) =
            (1..depth).map { if (size > 0) stack.removeAt(stack.size - 1) else NullValue }.first()

        override fun dup(times: Int) = if (size > 0) (1..times).forEach { _ -> push(peek()) } else Unit
        override fun peek(depth: Int): Value = if (size >= depth) stack[size - depth] else NullValue
        override fun swap() = if (size > 1) swap(stack, size - 1, size - 2) else Unit
        override fun clear() = stack.clear()

        override fun push(any: Any?) {
            stack.add(any.value())
        }

        companion object {
            private fun <T> swap(list: MutableList<T>, i: Int, j: Int) {
                list[i] = list.set(j, list[i])
            }
        }
    }
}