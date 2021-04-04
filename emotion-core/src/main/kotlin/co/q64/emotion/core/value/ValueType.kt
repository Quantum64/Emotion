package co.q64.emotion.core.value

typealias any = AnyType
typealias num = NumberType
typealias list = ListType
typealias str = StringType
typealias type = TypeType

sealed interface ValueType {
    val name: String
}

object AnyType : ValueType {
    override val name get() = "any"
}

object NumberType : ValueType {
    override val name get() = "number"
}

object ListType : ValueType {
    override val name: String get() = "list"
}

object StringType : ValueType {
    override val name: String get() = "string"
}

object TypeType : ValueType {
    override val name: String get() = "type"
}

object NullType : ValueType {
    override val name: String get() = "null"
}

data class IntersectType(
    val types: Set<ValueType>
) : ValueType {
    override val name: String get() = types.joinToString("|") { it.name }
}

infix fun ValueType.or(type: ValueType) = IntersectType(children + type.children)

val ValueType.children
    get() = when (this) {
        is IntersectType -> types
        else -> setOf(this)
    }

val BaseTypes = listOf(
    AnyType,
    NumberType,
    ListType,
    StringType,
    TypeType
)