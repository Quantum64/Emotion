package co.q64.emotion.lang.value

open class ValueType
object NumberType : ValueType()
object BooleanType : ValueType()
object ListType : ValueType()
object StringType : ValueType()
object NullType : ValueType()

val any = ValueType()
val number = NumberType
val boolean = BooleanType
val list = ListType
val string = StringType
val nul = NullType