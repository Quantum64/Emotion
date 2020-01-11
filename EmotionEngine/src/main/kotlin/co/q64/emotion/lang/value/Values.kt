package co.q64.emotion.lang.value

import co.q64.emotion.lang.Operation
import co.q64.emotion.math.Rational

class BooleanValue(val value: Boolean) : Value {
    override fun operate(other: Value, type: Operation): Value =
            if (other is BooleanValue) {
                when (type) {
                    Operation.ADD -> BooleanValue(other.value || value)
                    Operation.SUBTRACT -> BooleanValue(!(other.value || value))
                    Operation.MULTIPLY -> BooleanValue(other.value && value)
                    Operation.DIVIDE -> BooleanValue(!(other.value && value))
                }
            } else this

    override val list: List<Value> get() = listOf(this)
    override fun compareTo(other: Value): Int = value.compareTo(other.boolean)
    override fun toString(): String = value.toString()
    override val number: Rational = when (value) {
        true -> Rational.ONE
        false -> Rational.ZERO
    }
}

class NumberValue(val value: Rational) : Value {
    override fun operate(other: Value, type: Operation): Value =
            if (other is NumberValue) {
                when (type) {
                    Operation.ADD -> value(value.add(other.number))
                    Operation.SUBTRACT -> value(value.subtract(other.number))
                }
            } else {

            }
    override val number get() = value
    override val list get() = (0 until value.intValue()).map { value(it) }
    override fun compareTo(other: Value): Int = value.compareTo(other.number)
    override fun toString(): String = value.toString()
}

class TextValue(val value: String) : Value {
    override fun operate(other: Value, type: Operation): Value =
            value(when {
                type == Operation.MULTIPLY && other is NumberValue -> value.repeat(other.int)
                type == Operation.SUBTRACT -> other.toString() + value
                else -> value + other.toString()
            })

    override fun compareTo(other: Value): Int = when (other) {
        is NumberValue -> value.length.compareTo(other.int)
        else -> value.compareTo(other.text)
    }

    override val number: Rational = Rational(value.length)
    override val list: List<Value> get() = value.map { value(it.toString()) }
    override fun toString(): String = value
}