package co.q64.emotion.core.value

import co.q64.emotion.core.math.rational

data class ListValue(
    private val value: List<Value>
) : Value {
    override val type get() = ListType
    override val number get() = value.size.rational()
}