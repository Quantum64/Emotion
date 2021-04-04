package co.q64.emotion.core.value

import co.q64.emotion.core.lang.value.ListValue
import co.q64.emotion.core.lang.value.NullValue
import co.q64.emotion.core.lang.value.NumberValue
import co.q64.emotion.core.lang.value.StringValue
import co.q64.emotion.core.math.Rational
import co.q64.emotion.core.math.rational

interface Value : Comparable<Value> {
    val type: ValueType

    val string get() = toString()
    val list: List<Value> get() = listOf(this)
    val number: Rational
    val int: Int get() = number.intValue()
    val long: Long get() = number.longValue()
    val double: Double get() = number.doubleValue()
    val boolean: Boolean get() = number != Rational.ZERO

    companion object {
        val Null get() = NullValue
        val True = 1.value()
        val False = 0.value()
    }
}

fun Rational.value(): Value = NumberValue(this)
fun Int.value(): Value = rational().value()
fun Long.value(): Value = rational().value()
fun Double.value(): Value = rational().value()
fun Boolean.value(): Value = (if (this) 1 else 0).value()
fun String.value(): Value = StringValue(this)
fun List<Value>.value(): Value = ListValue(this)

fun Any?.value(): Value = when (this) {
    is Value -> this
    is Rational -> value()
    is Int -> value()
    is Long -> value()
    is Double -> value()
    is Boolean -> value()
    is String -> value()
    is List<*> -> map { it.value() }.value()
    null -> NullValue
    else -> toString().value()
}