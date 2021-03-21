package co.q64.emotion.core.value

import co.q64.emotion.core.math.Rational
import co.q64.emotion.core.math.rational

interface Value {
    val type: ValueType

    val string get() = toString()
    val list: List<Value> get() = listOf(this)
    val number: Rational
    val int: Int get() = number.intValue()
    val long: Long get() = number.longValue()
    val double: Double get() = number.doubleValue()
    val boolean: Boolean get() = number != Rational.ZERO
}

fun Rational.value(): Value = NumberValue(this)
fun Int.value(): Value = rational().value()
fun Long.value(): Value = rational().value()
fun Double.value(): Value = rational().value()
fun Boolean.value(): Value = (if (this) 1 else 0).value()
fun List<Value>.value(): Value = ListValue(this)

fun Any?.value(): Value = when (this) {
    is Value -> this
    is Rational -> value()
    is Int -> value()
    is Long -> value()
    is Double -> value()
    is Boolean -> value()
    is List<*> -> map { it.value() }.value()
    else -> toString().value()
}