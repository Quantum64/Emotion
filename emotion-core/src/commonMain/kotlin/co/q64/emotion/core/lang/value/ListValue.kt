package co.q64.emotion.core.lang.value

import co.q64.emotion.core.math.rational
import co.q64.emotion.core.value.ListType
import co.q64.emotion.core.value.Value

data class ListValue(
    private val value: List<Value>
) : Value {
    override val type get() = ListType
    override val number get() = value.size.rational()
    override val list: List<Value> get() = value

    override fun compareTo(other: Value): Int {
        TODO("not implemented")
    }

    override fun toString() = value.joinToString(",")
}