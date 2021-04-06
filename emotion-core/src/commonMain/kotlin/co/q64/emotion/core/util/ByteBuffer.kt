package co.q64.emotion.core.util

class ByteBuffer(size: Int) {
    private val internal: ByteArray = ByteArray(size)
    private var pointer: Int = 0

    fun put(b: Byte) {
        internal[pointer] = b
        pointer++
    }

    fun array(): ByteArray {
        return internal
    }

    fun pointer(): Int {
        return pointer
    }
}