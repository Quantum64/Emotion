package co.q64.emotion.core.value

import co.q64.emotion.core.lang.value.ListValue
import co.q64.emotion.core.lang.value.NullValue
import co.q64.emotion.core.lang.value.NumberValue
import co.q64.emotion.core.lang.value.StringValue
import co.q64.emotion.core.lang.value.TypeValue
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
fun ValueType.value(): Value = TypeValue(this)
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
    is ValueType -> value()
    null -> NullValue
    else -> toString().value()
}

fun listOfValues(vararg values: Any?) = values.map { it.value() }

fun parseLiteral(literal: String) =
    if (literal.isNotBlank() &&
        literal.all { it.isDigit() || it == '.' || it == '/' || it == 'e' } &&
        literal.asSequence().filter { it == '.' || it == '/' || it == 'e' }.count() <= 1 &&
        literal.toSet().let { set ->
            when {
                'e' in set -> literal.indexOf('e').let { it > 0 && it < literal.length - 1 }
                '.' in set -> literal.indexOf(".") < literal.length - 1
                '/' in set -> literal.indexOf('/').let { it > 0 && it < literal.length - 1 }
                else -> true
            }
        }
    ) Rational.parse(literal).value() else literal.value()

fun parseArguments(input: String): List<Value> {
    fun internal(iterator: CharIterator): List<Value> {
        fun completeList(): Value = internal(iterator)
            .value()

        fun completeQuote(): Value = iterator
            .asSequence()
            .takeWhile { it != '"' }
            .joinToString("")
            .value()

        val result = mutableListOf<Value>()
        var buffer = ""
        while (iterator.hasNext()) {
            val next = iterator.next()
            when {
                next.isWhitespace() -> continue
                next == '[' -> result += completeList()
                next == '"' -> result += completeQuote()
                next == ',' -> {
                    if (buffer.isNotBlank()) result += parseLiteral(buffer)
                    buffer = ""
                }
                next == ']' -> {
                    if (buffer.isNotBlank()) result += parseLiteral(buffer)
                    return result
                }
                else -> buffer += next
            }
        }
        if (buffer.isNotBlank()) result += parseLiteral(buffer)
        return result
    }
    return internal(input.iterator())
}