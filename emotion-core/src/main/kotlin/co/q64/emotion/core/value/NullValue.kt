package co.q64.emotion.core.value

import co.q64.emotion.core.math.rational

object NullValue : Value {
    override val type get() = AnyType
    override val number = 0.rational()

    override fun toString(): String = "null"
}