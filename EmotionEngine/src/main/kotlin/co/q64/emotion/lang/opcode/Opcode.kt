package co.q64.emotion.lang.opcode

import co.q64.emotion.lang.Program
import co.q64.emotion.lang.value.ValueType
import org.koin.core.error.NoParameterFoundException

class Opcode(val name: String, val description: String, val types: List<ValueType>, val function: Program.(values: OpcodeArguments) -> Any?)
class OpcodeBuilder(val name: String) {
    var description: String = "No description available."
    var types: List<ValueType> = listOf()
    var function: Program.(values: OpcodeArguments) -> Any? = error("Not implemented")
}

operator fun String.invoke(description: String, types: List<ValueType>, function: Program.(values: OpcodeArguments) -> Any?) =
        Tokens.register(Opcode(this, description, types, function))

operator fun String.invoke(description: String, function: Program.(values: OpcodeArguments) -> Any?) =
        Tokens.register(Opcode(this, description, listOf(), function))

operator fun String.invoke(builder: OpcodeBuilder.() -> Unit) = OpcodeBuilder(this)
        .apply { builder(this) }
        .run { Opcode(name, description, types, function) }
        .let { Tokens.register(it) }

@Suppress("UNCHECKED_CAST")
class OpcodeArguments internal constructor(vararg val values: Any?) {
    private fun <T> elementAt(i: Int): T =
            if (values.size > i) values[i] as T else throw NoParameterFoundException("Can't get parameter value #$i from $this")

    operator fun <T> component1(): T = elementAt(0)
    operator fun <T> component2(): T = elementAt(1)
    operator fun <T> component3(): T = elementAt(2)
    operator fun <T> component4(): T = elementAt(3)
    operator fun <T> component5(): T = elementAt(4)
    operator fun <T> get(i: Int) = values[i] as T

    fun <T> set(i: Int, t: T) {
        values.toMutableList()[i] = t
    }

    fun size() = values.size
    fun isEmpty() = size() == 0
    fun isNotEmpty() = !isEmpty()
    inline fun <reified T> get() = values.first { it is T }
}