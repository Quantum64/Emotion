package co.q64.emotion.core.value

sealed interface ValueType
object AnyType : ValueType
object NumberType : ValueType
object ListType : ValueType
object StringType : ValueType
data class IntersectType(
    val types: Set<ValueType>
) : ValueType

typealias any = AnyType
typealias num = NumberType
typealias list = ListType
typealias str = StringType

infix fun ValueType.or(type: ValueType) = IntersectType(children + type.children)

val ValueType.children
    get() = when (this) {
        is IntersectType -> types
        else -> setOf(this)
    }