package co.q64.emotion.core.lang.opcode

import co.q64.emotion.core.lang.value.TypeValue
import co.q64.emotion.core.opcode.OpcodeContainer
import co.q64.emotion.core.opcode.OpcodeRegistry
import co.q64.emotion.core.value.BaseTypes
import co.q64.emotion.core.value.any
import co.q64.emotion.core.value.or
import co.q64.emotion.core.value.type

object MetaOpcodes : OpcodeRegistry {
    override fun OpcodeContainer.register() {
        BaseTypes.forEach { value ->
            "pushtype ${value.name}"(
                "Push type ${value.name}.",
                push = type
            ) { push(value) }
        }

        "type.get"(
            "Push the type of $a.",
            any, push = type
        ) { push(pop().type) }

        "type.or"(
            "Push the intersection type of $a and $b.",
            type, type, push = type
        ) { push((pop() as TypeValue).value or (pop() as TypeValue).value) }
    }
}