package co.q64.emotion.core.extension

fun String.emotionCodePointCount(beginIndex: Int, endIndex: Int): Int {
    var count = endIndex - beginIndex
    var i = beginIndex
    while (i < endIndex - 1) {
        if (this[i++].isHighSurrogate() &&
            this[i].isLowSurrogate()
        ) {
            count--
            i++
        }
    }
    return count
}

fun String.emotionCodePointAt(index: Int): Int {
    var index = index
    val c1: Char = this[index]
    if (isHighSurrogate(c1) && ++index < length) {
        val c2: Char = this[index]
        if (isLowSurrogate(c2)) {
            return toCodePoint(c1, c2)
        }
    }
    return c1.toInt()
}

fun toChars(codePoint: Int): CharArray {
    fun toSurrogates(codePoint: Int, dst: CharArray, index: Int) {
        // We write elements "backwards" to guarantee all-or-nothing
        dst[index + 1] = lowSurrogate(codePoint)
        dst[index] = highSurrogate(codePoint)
    }

    return when {
        isBmpCodePoint(codePoint) -> {
            charArrayOf(codePoint.toChar())
        }
        isValidCodePoint(codePoint) -> {
            val result = CharArray(2)
            toSurrogates(codePoint, result, 0)
            result
        }
        else -> {
            error("Not a valid Unicode code point: $codePoint")
        }
    }
}

fun charCount(codePoint: Int): Int {
    return if (codePoint >= MIN_SUPPLEMENTARY_CODE_POINT) 2 else 1
}

private const val MIN_HIGH_SURROGATE = '\uD800'
private const val MAX_HIGH_SURROGATE = '\uDBFF'
private const val MIN_LOW_SURROGATE = '\uDC00'
private const val MAX_LOW_SURROGATE = '\uDFFF'
private const val MIN_SURROGATE = MIN_HIGH_SURROGATE
private const val MAX_SURROGATE = MAX_LOW_SURROGATE
private const val MIN_SUPPLEMENTARY_CODE_POINT = 0x010000
private const val MIN_CODE_POINT = 0x000000
private const val MAX_CODE_POINT = 0X10FFFF

private fun isBmpCodePoint(codePoint: Int): Boolean {
    return codePoint ushr 16 == 0
}

private fun lowSurrogate(codePoint: Int): Char {
    return ((codePoint and 0x3ff) + MIN_LOW_SURROGATE.toInt()).toChar()
}

private fun highSurrogate(codePoint: Int): Char {
    return ((codePoint ushr 10)
            + (MIN_HIGH_SURROGATE.toInt() - (MIN_SUPPLEMENTARY_CODE_POINT ushr 10))).toChar()
}


private fun isValidCodePoint(codePoint: Int): Boolean {
    val plane = codePoint ushr 16
    return plane < MAX_CODE_POINT + 1 ushr 16
}

private fun isHighSurrogate(ch: Char): Boolean {
    return ch >= MIN_HIGH_SURROGATE && ch.toInt() < MAX_HIGH_SURROGATE.toInt() + 1
}

private fun isLowSurrogate(ch: Char): Boolean {
    return ch >= MIN_LOW_SURROGATE && ch.toInt() < MAX_LOW_SURROGATE.toInt() + 1
}

private fun toCodePoint(high: Char, low: Char): Int {
    return (high.toInt() shl 10) + low.toInt() + (MIN_SUPPLEMENTARY_CODE_POINT
            - (MIN_HIGH_SURROGATE.toInt() shl 10)
            - MIN_LOW_SURROGATE.toInt())
}