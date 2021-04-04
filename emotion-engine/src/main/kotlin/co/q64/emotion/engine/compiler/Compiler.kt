package co.q64.emotion.engine.compiler

import co.q64.emotion.engine.compress.Compressors
import co.q64.emotion.engine.opcode.Opcodes

object Compiler {
    data class Line(
        val raw: String,
        val value: String,
        val data: String?,
        val index: Int
    )

    fun compile(program: String) =
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
                            instruction = line.raw,
                            description = "Push literal ${line.data}",
                            encoded = Compressors.compress(line.data),
                            line = line.index
                        )
                    else -> {
                        opcode ?: error("Invalid instruction '${line.raw}' in source. Line: ${line.index}")
                        CompiledInstruction(
                            instruction = line.raw,
                            description = opcode.description,
                            encoded = Opcodes.encode(opcode),
                            line = line.index
                        )
                    }
                }

            }
            .toList()
}

fun main() {
    val prog = """
        load this is a test2243575432078953427980
        str.toUpperCase
        load more
        num.concat
    """.trimIndent()
    println(Compiler.compile(prog).joinToString("") { it.encoded })
}