package co.q64.emotion.lang

import co.q64.emotion.lang.value.NullValue
import co.q64.emotion.lang.value.Value

class Registers {
    var a: Value = NullValue
    var b: Value = NullValue
    var c: Value = NullValue
    var d: Value = NullValue
    var i: Value = NullValue
    var o: Value = NullValue
}

class Memory {
    val data: MutableMap<Value, Value> = mutableMapOf()
    operator fun get(address: Value) = data[address] ?: NullValue
    operator fun set(address: Value, value: Value) {
        data[address] = value
    }
}