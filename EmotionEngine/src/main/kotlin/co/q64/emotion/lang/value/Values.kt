package co.q64.emotion.lang.value

import co.q64.emotion.lang.Operation
import co.q64.emotion.lang.Operation.*
import co.q64.emotion.math.Rational
import co.q64.emotion.math.rational
import java.util.Collections


class BooleanValue(val value: Boolean) : Value {
    override val type get() = booleanType
    override val list get() = listOf(this)
    override fun compareTo(other: Value) = value.compareTo(other.boolean)
    override fun toString() = value.toString()
    override val number = when (value) {
        true -> Rational.ONE
        false -> Rational.ZERO
    }

    override fun operate(other: Value, type: Operation): Value =
            if (other is BooleanValue) when (type) {
                ADD -> (other.value || value).value()
                SUBTRACT -> (!(other.value || value)).value()
                MULTIPLY -> (other.value && value).value()
                DIVIDE -> (!(other.value && value)).value()
            } else this
}

class NumberValue(val value: Rational) : Value {
    override val type get() = numberType
    override val number get() = value
    override val list get() = (0 until value.intValue()).map { it.value() }
    override fun compareTo(other: Value) = value.compareTo(other.number)
    override fun toString() = value.toString()
    override fun operate(other: Value, type: Operation) =
            if (other is NumberValue) when (type) {
                ADD -> value.add(other.number).value()
                SUBTRACT -> value.subtract(other.number).value()
                MULTIPLY -> value.multiply(other.number).value()
                DIVIDE -> value.divide(other.number).value()
            } else text.value().operate(other, type)
}

class TextValue(val value: String) : Value {
    override val type get() = stringType
    override val number: Rational = value.length.rational()
    override val list: List<Value> get() = value.map { it.toString().value() }
    override fun toString(): String = value

    override fun operate(other: Value, type: Operation): Value =
            when {
                type == MULTIPLY && other is NumberValue -> value.repeat(other.int)
                type == SUBTRACT -> other.toString() + value
                else -> value + other.toString()
            }.value()

    override fun compareTo(other: Value): Int = when (other) {
        is NumberValue -> value.length.compareTo(other.int)
        else -> value.compareTo(other.text)
    }
}

class ListValue(val value: List<Value>) : Value {
    override val type get() = listType
    override val number get() = value.size.rational()
    override val list: List<Value> = value
    override fun compareTo(other: Value): Int = if (list == other.list) 0 else -1

    override fun operate(other: Value, type: Operation): Value =
            if (other is NumberValue && type == Operation.MULTIPLY)
                Collections.nCopies(other.int, value).flatten().value()
            else when (type) {
                ADD -> (list + listOf(other)).value()
                SUBTRACT -> list.filter { it != value }.value()
                else -> this
            }
}

object NullValue : Value {
    override val type get() = nullType
    override val list = listOf(this)
    override val number = Rational.ZERO
    override fun operate(other: Value, type: Operation) = this
    override fun compareTo(other: Value) = if (other is NullValue) 0 else -1
}

private val booleanType = boolean
private val stringType = string
private val numberType = number
private val listType = list
private val nullType = nul