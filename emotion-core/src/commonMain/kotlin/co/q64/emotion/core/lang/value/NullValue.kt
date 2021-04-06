package co.q64.emotion.core.lang.value

import co.q64.emotion.core.math.rational
import co.q64.emotion.core.value.NullType
import co.q64.emotion.core.value.Value
import co.q64.emotion.core.value.value

object  NullValue : Value {
    override val type get() = NullType
    override val number = 0.rational()
    override fun compareTo(other: Value) = number.value().compareTo(other)
    override fun toString(): String = ""

}