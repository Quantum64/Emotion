package co.q64.emotion.engine.compiler

import co.q64.emotion.core.opcode.Opcode
import co.q64.emotion.core.value.children
import co.q64.emotion.engine.compress.Compressors
import co.q64.emotion.engine.opcode.Opcodes

object Compiler {
    data class Line(
        val raw: String,
        val value: String,
        val data: String?,
        val index: Int
    )

    fun compile(program: String, assertions: Boolean = false): List<CompiledInstruction> =
        program
            .lineSequence()
            .map { it.trimStart() }
            .filter { it.isNotBlank() }
            .withIndex()
            .map { (index, value) ->
                value.split(" ").let { parts ->
                    Line(
                        raw = value,
                        value = parts.first(),
                        data = parts.takeIf { it.size > 1 }?.drop(1)?.joinToString(" "),
                        index = index + 1
                    )
                }
            }
            .map { line ->
                val opcode = Opcodes.find(line.raw) ?: Opcodes.find(line.value)
                when {
                    opcode == null && line.data != null && line.value == "load" ->
                        CompiledInstruction(
                            opcode = null,
                            instruction = line.raw,
                            description = "Push literal ${line.data}",
                            encoded = Compressors.compress(line.data),
                            line = line.index
                        )
                    else -> {
                        opcode ?: error("Invalid instruction '${line.raw}' in source. Line: ${line.index}")
                        val encoded = (if (assertions) generateTypeAssertion(opcode) else "") + Opcodes.encode(opcode)
                        CompiledInstruction(
                            opcode = opcode,
                            instruction = line.raw,
                            description = opcode.description,
                            encoded = encoded,
                            line = line.index
                        )
                    }
                }
            }
            .toList()
            .let { Lowerings.lower(it) }

    private fun generateTypeAssertion(opcode: Opcode) =
        opcode.values.flatMap { type ->
            type.children
                .map { "pushtype ${it.name}" }
                .flatMapIndexed { index, instruction ->
                    if (index == 0) listOf(instruction)
                    else listOf(instruction, "type.or")
                }
        }
            .plus("load ${opcode.values.size}")
            .plus("debug.asserttypes")
            .joinToString("\n")
            .let { compile(it) }
            .joinToString("") { it.encoded }

}