package co.q64.emotion.core.lang.value

import co.q64.emotion.core.math.Rational
import co.q64.emotion.core.math.rational
import co.q64.emotion.core.value.StringType
import co.q64.emotion.core.value.Value
import co.q64.emotion.core.value.value

data class StringValue(
    private val value: String
) : Value {
    override val type get() = StringType
    override val number: Rational get() = value.length.rational()
    override val list: List<Value> get() = value.toList().map { "$it".value() }
    override fun compareTo(other: Value): Int = value.compareTo(other.string)
    override fun toString(): String = value
}