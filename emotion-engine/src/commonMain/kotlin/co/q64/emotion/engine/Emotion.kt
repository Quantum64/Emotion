package co.q64.emotion.engine

import co.q64.emotion.core.lang.Instruction
import co.q64.emotion.core.lang.Program
import co.q64.emotion.core.runtime.Environment
import co.q64.emotion.engine.compiler.Compiler
import co.q64.emotion.engine.lexer.Lexer

data class Emotion(
    private val environment: Environment
) {
    fun compile(lines: String, assertions: Boolean = false) =
        Compiler.compile(lines, assertions).joinToString("") { it.encoded }

    fun run(program: String, arguments: String = "") = run(Lexer.parse(program), arguments)
    fun run(instructions: List<Instruction>, arguments: String = "") =
        Program(environment, instructions, arguments).start()
}