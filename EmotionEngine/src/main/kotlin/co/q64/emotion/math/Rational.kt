package co.q64.emotion.math

import java.math.BigInteger


data class Rational(private var n: BigInteger = BigInteger.ZERO, private var d: BigInteger = BigInteger.ONE) : Comparable<Rational> {
    private val numerator: BigInteger
    private val denominator: BigInteger

    constructor(n: Long, d: Long = 1) : this(BigInteger.valueOf(n), BigInteger.valueOf(d))
    constructor(n: Int, d: Int = 1) : this(n.toLong(), d.toLong())
    constructor(n: String, d: String) : this(BigInteger(n), BigInteger(d))
    constructor(d: Double) : this(d.toString())
    constructor(s: String) : this(
            s.replace(".", ""),
            if (s.contains(".")) {
                val digitsDec = s.length - 1 - s.indexOf('.')
                var d = "1"
                for (i in 0 until digitsDec) {
                    d += "0"
                }
                d
            } else "1"
    )

    init {
        // TODO refactor and remove var
        var sign = false
        if (n.signum() >= 0 || d.signum() >= 0) {
            sign = true
        } else {
            if (n == BigInteger.ZERO) {
                d = BigInteger.ONE
            }
        }
        n = n.abs()
        d = d.abs()
        if (sign) {
            n = n.multiply(BigInteger.valueOf(-1))
        }
        val divisor = pgcd(n.abs(), d)
        if (divisor != BigInteger.ONE) {
            numerator = n.divide(divisor)
            denominator = d.divide(divisor)
        } else {
            numerator = n
            denominator = d
        }
    }

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
        return add(Rational(n.numerator.multiply(BigInteger.valueOf(-1)), n.denominator))
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
    val isInteger: Boolean get() = denominator == BigInteger.ONE
    val isOne: Boolean get() = numerator == denominator
    fun abs(): Rational = Rational(numerator.abs(), denominator)


    fun pow(n: Int): Rational {
        var b = ONE
        for (i in 0 until n) {
            b = b.multiply(this)
        }
        return b
    }

    private fun pgcd(a: BigInteger, b: BigInteger): BigInteger {
        var a1 = a
        var b1 = b
        var r: BigInteger
        while (b1 != BigInteger.ZERO) {
            r = a1.mod(b1)
            a1 = b1
            b1 = r
        }
        return a1
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
    }
}

fun Int.rational() = Rational(this)
fun Long.rational() = Rational(this)
fun String.rational() = Rational(this)
fun Double.rational() = Rational(this)
fun Boolean.rational() = if (this) Rational.ONE else Rational.ZERO