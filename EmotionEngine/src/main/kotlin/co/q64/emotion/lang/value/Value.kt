package co.q64.emotion.lang.value

import co.q64.emotion.lang.Operation
import co.q64.emotion.math.Rational
import co.q64.emotion.math.rational

interface Value : Comparable<Value> {
    fun operate(other: Value, type: Operation): Value
    val list: List<Value>
    val number: Rational
    val int: Int get() = number.intValue()
    val long: Long get() = number.longValue()
    val double: Double get() = number.doubleValue()
    val boolean: Boolean get() = number != Rational.ZERO
    val text: String get() = toString()
}

fun value(value: String): Value {
    TODO()
}
fun Rational.value(): Value = NumberValue(this)
fun Int.value(value: Int): Value = rational().value()
fun Long.value(value: Long): Value = rational().value()
fun Double.value(): Value = rational().value()
fun Value.value() = if (this is TextValue) value(this.text) else this