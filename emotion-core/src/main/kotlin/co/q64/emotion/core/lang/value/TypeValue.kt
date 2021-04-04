package co.q64.emotion.core.lang.value

import co.q64.emotion.core.math.Rational
import co.q64.emotion.core.value.TypeType
import co.q64.emotion.core.value.Value
import co.q64.emotion.core.value.ValueType

class TypeValue(
    val value: ValueType
) : Value {
    override val type: ValueType get() = TypeType
    override val number: Rational get() = Rational.ZERO
    override fun compareTo(other: Value): Int = number.compareTo(other.number)
}