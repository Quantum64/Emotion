package co.q64.emotion.core.lang

import co.q64.emotion.core.ast.AST
import co.q64.emotion.core.ast.ASTBreak
import co.q64.emotion.core.ast.ASTCondition
import co.q64.emotion.core.ast.ASTContinue
import co.q64.emotion.core.ast.ASTEmpty
import co.q64.emotion.core.ast.ASTInstruction
import co.q64.emotion.core.ast.ASTIterator
import co.q64.emotion.core.ast.ASTNode
import co.q64.emotion.core.opcode.Compare
import co.q64.emotion.core.opcode.Control
import co.q64.emotion.core.opcode.Iterate
import co.q64.emotion.core.runtime.Environment

class Program(
    val environment: Environment,
    instructions: List<Instruction>,
    val memory: Memory = Memory(),
    private var time: Long = 0,
    private var stopped: Boolean = false
) : Stack by Stack.Simple() {
    private val ast: ASTNode

    init {
        ast = buildAST(instructions.iterator())
    }

    fun start() {
        time = System.currentTimeMillis()
        ast.enter()
    }

    fun terminated(): Boolean {
        if (!stopped && System.currentTimeMillis() - MaximumExecutionTime > time) {
            crash("Execution timed out. ($MaximumExecutionTime ms)")
        }
        return stopped
    }

    fun stop(print: Boolean = true) {
        if (!terminated()) {
            stopped = true
            if (print) {
                environment.output.println(pop().toString())
            }
        }
    }

    fun crash(message: String) {
        if (!terminated()) {
            environment.output.println(
                """
                    Fatal: $message
                    The program cannot continue and will now terminate.
                """.trimIndent()
            )
            stop(print = false)
        }
    }

    private fun buildAST(instructions: Iterator<Instruction>): ASTNode {
        fun buildASTEx(instructions: Iterator<Instruction>): Pair<ASTNode, ASTDirective> {
            val result = mutableListOf<ASTNode>()
            while (instructions.hasNext()) {
                val next = instructions.next()
                when (val marker = next.marker) {
                    Control.End -> return AST(result) to ASTDirective.End
                    Control.Else -> return AST(result) to ASTDirective.Else
                    Control.Break -> result += ASTBreak
                    Control.Continue -> result += ASTContinue
                    is Compare -> buildASTEx(instructions)
                        .let { (pass, directive) ->
                            result += ASTCondition(
                                program = this,
                                type = marker,
                                pass = pass,
                                fail = when (directive) {
                                    ASTDirective.Else -> buildAST(instructions)
                                    else -> ASTEmpty
                                }
                            )
                        }
                    is Iterate -> result += ASTIterator(
                        program = this,
                        body = buildAST(instructions),
                        type = marker
                    )
                    else -> result += ASTInstruction(this, next)
                }
            }
            return AST(result) to ASTDirective.None
        }
        return buildASTEx(instructions).let { (node, _) -> node }
    }

    enum class ASTDirective {
        None, Else, End
    }

    companion object {
        const val MaximumExecutionTime = 5000L
    }
}