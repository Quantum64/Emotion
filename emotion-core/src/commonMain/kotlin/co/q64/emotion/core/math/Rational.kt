package co.q64.emotion.core.math

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlin.math.absoluteValue
import kotlin.math.sign


class Rational(n: Component, d: Component = Component.One) : Comparable<Rational> {
    private val numerator: Component
    private val denominator: Component

    init {
        var mn: Component
        var md = d
        var sign = false
        if (n.signum() < 0 || d.signum() < 0) {
            sign = true
        } else {
            if (n == Component.Zero) {
                md = Component.One
            }
        }
        mn = n.abs()
        md = md.abs()
        if (sign) {
            mn = mn.multiply(Component.MinusOne)
        }
        val divisor = Component.gcd(mn.abs(), md)
        if (divisor != Component.One) {
            numerator = mn.divide(divisor)
            denominator = md.divide(divisor)
        } else {
            numerator = mn
            denominator = md
        }
    }

    constructor(n: Long, d: Long = 1) : this(Component.create(n), Component.create(d))
    constructor(n: Int, d: Int = 1) : this(n.toLong(), d.toLong())
    constructor(n: String, d: String) : this(
        Component.create(BigInteger.parseString(n)),
        Component.create(BigInteger.parseString(d))
    )

    fun intValue(): Int = numerator.toInt() / denominator.toInt()
    fun longValue(): Long = numerator.toLong() / denominator.toLong()
    fun floatValue(): Float = numerator.toFloat() / denominator.toFloat()
    fun doubleValue(): Double = numerator.toDouble() / denominator.toDouble()

    fun add(n: Rational): Rational {
        var num1 = numerator
        val denom1 = denominator
        var num2 = n.numerator
        val denom2 = n.denominator
        num1 = num1.multiply(denom2)
        num2 = num2.multiply(denom1)
        val denom = denom1.multiply(denom2)
        return Rational(num1.add(num2), denom)
    }

    fun subtract(n: Rational): Rational {
        return add(Rational(n.numerator.multiply(Component.MinusOne), n.denominator))
    }

    fun multiply(n: Rational): Rational {
        var num1 = numerator
        var denom1 = denominator
        val num2 = n.numerator
        val denom2 = n.denominator
        num1 = num1.multiply(num2)
        denom1 = denom1.multiply(denom2)
        return Rational(num1, denom1)
    }

    fun divide(n: Rational): Rational = multiply(Rational(n.denominator, n.numerator))
    fun reciprocal(): Rational = ONE.divide(this)
    val isInteger: Boolean get() = denominator == Component.One
    val isOne: Boolean get() = numerator == denominator
    fun abs(): Rational = Rational(numerator.abs(), denominator)

    fun pow(n: Int): Rational {
        var b = ONE
        for (i in 0 until n) {
            b = b.multiply(this)
        }
        return b
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Rational) return false
        return numerator == other.numerator && denominator == other.denominator
    }

    override fun hashCode(): Int {
        return numerator.hashCode() + denominator.hashCode()
    }

    override fun toString(): String {
        return if (denominator.toLong() == 1L) {
            numerator.toString()
        } else "$numerator/$denominator"
    }

    override operator fun compareTo(other: Rational): Int {
        val a = numerator.multiply(other.denominator)
        val b = denominator.multiply(other.numerator)
        return a.compareTo(b)
    }

    companion object {
        val ZERO = Rational(0)
        val ONE = Rational(1)
        val MINUS_ONE = Rational(-1)

        fun parse(value: String) = when {
            'e' in value -> Rational(Component.create(BigDecimal.parseString(value).toBigInteger()))
            '/' in value -> {
                value.split("/").let { parts ->
                    Rational(parts[0], parts.getOrElse(1) { "0" })
                }
            }
            else -> {
                Rational(
                    value.replace(".", ""),
                    if (value.contains(".")) {
                        val digitsDec = value.length - 1 - value.indexOf('.')
                        var d = "1"
                        for (i in 0 until digitsDec) {
                            d += "0"
                        }
                        d
                    } else "1"
                )
            }
        }

        fun parse(value: Double) = parse(value.toString())
    }

    sealed interface Component : Comparable<Component> {
        companion object {
            fun create(value: Long) = Short(value)
            fun create(value: BigInteger) = Big(value).optimize()

            val One = create(1)
            val Zero = create(0)
            val MinusOne = create(-1)

            tailrec fun gcd(a: Component, b: Component): Component = when (b) {
                Zero -> a
                else -> gcd(b, a.mod(b))
            }

            private fun addExact(x: Long, y: Long): Long {
                val r = x + y
                if (x xor r and (y xor r) < 0) {
                    throw ArithmeticException("long overflow")
                }
                return r
            }

            private fun multiplyExact(x: Long, y: Long): Long {
                val r = x * y
                val ax: Long = x.absoluteValue
                val ay: Long = y.absoluteValue
                if (ax or ay ushr 31 != 0L) {
                    if (y != 0L && r / y != x ||
                        x == Long.MIN_VALUE && y == -1L
                    ) {
                        throw ArithmeticException("long overflow")
                    }
                }
                return r
            }

        }

        val Long.component get() = Short(this)
        val BigInteger.component get() = Big(this).optimize()

        val big: BigInteger
        fun optimize(): Component
        fun abs(): Component
        fun signum(): Int
        fun add(component: Component): Component
        fun multiply(component: Component): Component
        fun divide(component: Component): Component
        fun mod(component: Component): Component

        fun toInt(): Int
        fun toLong(): Long
        fun toFloat(): Float
        fun toDouble(): Double

        class Short(
            val value: Long
        ) : Component {
            override val big: BigInteger get() = BigInteger.fromLong(value)
            override fun abs(): Component = value.absoluteValue.component
            override fun signum(): Int = value.sign
            override fun optimize(): Component = this
            override fun hashCode(): Int = value.hashCode()
            override fun toString(): String = value.toString()
            override fun compareTo(other: Component): Int = when (other) {
                is Short -> value.compareTo(other.value)
                else -> big.compareTo(other.big)
            }

            override fun add(component: Component) = when (component) {
                is Short -> runCatching { addExact(value, component.value).component }
                    .getOrElse { big.add(component.big).component }
                else -> big.add(component.big).component
            }

            override fun multiply(component: Component) = when (component) {
                is Short -> runCatching { multiplyExact(value, component.value).component }
                    .getOrElse { big.multiply(component.big).component }
                else -> big.multiply(component.big).component
            }

            override fun divide(component: Component) = when (component) {
                is Short -> (value / component.value).component
                else -> big.divide(component.big).component
            }

            override fun mod(component: Component): Component = when (component) {
                is Short -> (value % component.value).component
                else -> big.mod(component.big).component
            }

            override fun equals(other: Any?): Boolean {
                if (other !is Component) return false
                if (other is Short) return value == other.value
                return big == other.big
            }

            override fun toInt() = value.toInt()
            override fun toLong() = value
            override fun toFloat() = value.toFloat()
            override fun toDouble() = value.toDouble()
        }

        class Big(
            val value: BigInteger
        ) : Component {
            override val big: BigInteger get() = value
            override fun abs(): Component = value.abs().component
            override fun signum(): Int = value.signum()
            override fun optimize(): Component =
                runCatching { value.longValue(exactRequired = true).component }.getOrElse { this }

            override fun add(component: Component) = value.add(component.big).component
            override fun multiply(component: Component) = value.multiply(component.big).component
            override fun divide(component: Component) = value.divide(component.big).component
            override fun mod(component: Component): Component = value.mod(component.big).component
            override fun compareTo(other: Component): Int = big.compareTo(other.big)
            override fun hashCode(): Int = value.hashCode()
            override fun toString(): String = value.toString()

            override fun equals(other: Any?): Boolean {
                if (other !is Component) return false
                return big == other.big
            }

            override fun toInt() = value.intValue()
            override fun toLong() = value.longValue()
            override fun toFloat() = value.floatValue()
            override fun toDouble() = value.doubleValue()
        }
    }
}

fun Int.rational() = Rational(this)
fun Long.rational() = Rational(this)
fun String.rational() = Rational.parse(this)
fun Double.rational() = Rational.parse(this)
fun Boolean.rational() = if (this) Rational.ONE else Rational.ZERO