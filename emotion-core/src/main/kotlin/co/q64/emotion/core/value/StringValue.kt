package co.q64.emotion.core.value

import co.q64.emotion.core.math.Rational
import co.q64.emotion.core.math.rational

data class StringValue(
    private val value: String
) : Value {
    override val type get() = StringType
    override val number: Rational get() = value.length.rational()
}