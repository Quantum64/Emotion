package co.q64.emotion.core.lang.value

import co.q64.emotion.core.math.Rational
import co.q64.emotion.core.value.NumberType
import co.q64.emotion.core.value.Value
import co.q64.emotion.core.value.value

data class NumberValue(
    private val value: Rational
) : Value {
    override val type get() = NumberType
    override val number get() = value
    override val list: List<Value> get() = (0 until value.intValue()).map { it.value() }

    override fun compareTo(other: Value): Int = number.compareTo(other.number)
    override fun toString(): String = value.toString()
}