package co.q64.emotion.core.value

import co.q64.emotion.core.math.Rational

data class NumberValue(
    private val value: Rational
) : Value {
    override val type get() = NumberType
    override val number get() = value

    override fun toString(): String = value.toString()
}