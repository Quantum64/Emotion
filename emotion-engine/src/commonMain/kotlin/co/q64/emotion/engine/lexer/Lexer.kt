package co.q64.emotion.engine.lexer

import co.q64.emotion.core.lang.Instruction
import co.q64.emotion.core.opcode.Compress
import co.q64.emotion.core.util.Codepage
import co.q64.emotion.core.value.parseLiteral
import co.q64.emotion.engine.compiler.Lowerings
import co.q64.emotion.engine.compress.Compressors
import co.q64.emotion.engine.opcode.Opcodes

object Lexer {
    fun parse(program: String): List<Instruction> {
        val iterator = Codepage.readString(program)
            .iterator()
        return iterator
            .asSequence()
            .fold(emptyList<Instruction>() to emptyList<Codepage>()) { (result, buffer), next ->
                if (Opcodes.isInstruction(buffer + next)) {
                    Opcodes.decode(buffer + next).let { pending ->
                        result + when (val marker = pending.marker) {
                            is Compress -> Instruction.Load(
                                parseLiteral(Compressors.decompress(marker, iterator))
                            )
                            else -> Instruction.Execute(pending)
                        } to emptyList()
                    }
                } else result to buffer + next
            }
            .let { (instructions, _) -> instructions }
            .let { Lowerings.raise(it) }
    }
}