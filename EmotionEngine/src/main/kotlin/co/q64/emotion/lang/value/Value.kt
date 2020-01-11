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

fun Rational.value(): Value = NumberValue(this)
fun Int.value(): Value = rational().value()
fun Long.value(): Value = rational().value()
fun Double.value(): Value = rational().value()
fun Boolean.value(): Value = BooleanValue(this)
fun List<Value>.value(): Value = ListValue(this)
fun Value.value() = if (this is TextValue) text.value() else this

fun String.value(): Value {
    if (contains("/") && length > indexOf("/")) runCatching {
        return Rational(substring(0, indexOf("/")), substring(indexOf("/") + 1)).value()
    }
    runCatching { return Rational(this).value() }
    if (equals("true", ignoreCase = true)) return true.value()
    if (equals("false", ignoreCase = true)) return false.value()
    if (startsWith("[") && endsWith("]"))
        return substring(1, length - 2).split(",").map(String::value).value()
    return TextValue(this)
}