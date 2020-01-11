package co.q64.emotion.lang.value

data class ValueType(val name: String)

val number = ValueType("number")
val boolean = ValueType("boolean")
val list = ValueType("list")
val string = ValueType("string")
val nul = ValueType("null")