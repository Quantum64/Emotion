package co.q64.emotion.core.value

sealed class ValueType
object AnyType : ValueType()
object NumberType : ValueType()
object ListType : ValueType()
object StringType : ValueType()

val any = AnyType
val number = NumberType
val list = ListType
val string = StringType