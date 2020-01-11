package co.q64.emotion.lang.opcode

import co.q64.emotion.lang.Program
import co.q64.emotion.lang.value.Value

class Opcode(val name: String, val description: String, val types: List<String>, val function: Program.(values: List<Value>) -> Any?) {
    operator fun unaryPlus() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

fun test() {
    +Opcode(
            name = "name",
            description = "f",
            types = listOf("s"),
            function = {
                null
            }
    )
}