package co.q64.emotion.ast

import co.q64.emotion.lang.Comparison
import co.q64.emotion.lang.Comparison.*
import co.q64.emotion.lang.Program
import co.q64.emotion.lang.value.Value
import co.q64.emotion.lang.value.value

interface ASTNode {
    fun visit(): ASTResult?
}

class AST(private val nodes: List<ASTNode>) : ASTNode {
    override fun visit(): ASTResult? {
        nodes.forEach { node -> node.visit()?.let { return it } }
        return null
    }

    override fun toString() = "[${nodes.joinToString()}]"
}

class ASTConditional(private val program: Program,
                     private val pass: AST,
                     private val fail: AST,
                     private val type: Comparison,
                     private val push: Value? = null) : ASTNode {
    override fun visit(): ASTResult? {
        push?.let { program.stack.push(it) }
        val b = program.stack.pop()
        val a = program.stack.pop()
        return if (when (type) {
                    GREATER -> a > b
                    LESS -> a < b
                    EQUAL -> a == b
                    EQUAL_GREATER -> a >= b
                    EQUAL_LESS -> a <= b
                    NOT_EQUAL -> a != b
                }) pass.visit() else fail.visit()
    }

    override fun toString() = "{if ${type.name} $pass else $fail}"
}

class ASTIterator(private val program: Program, private val body: ASTNode, private val push: Boolean) : ASTNode {
    override fun visit(): ASTResult? = with(program) {
        stack.pop().list.forEachIndexed { index, value ->
            registers.i = index.value()
            registers.o = value
            if (push) stack.push(value)
            body.visit()?.let { if (it == ASTResult.BREAK) return null }
        }
        return null
    }

    override fun toString() = "(iterate $body)"
}

class ASTFunction(private val body: ASTNode) : ASTNode {
    fun enter() = body.visit()
    override fun visit(): ASTResult? = null
    override fun toString() = "<function $body>"
}

class ASTLoop(private val program: Program, private val body: ASTNode) : ASTNode {
    override fun visit(): ASTResult? {
        while (true) body.visit()?.let { if (it == ASTResult.BREAK) return null }
    }

    override fun toString() = "(loop $body)"
}

class ASTBreakToken : ASTNode {
    override fun visit() = ASTResult.BREAK
}

class ASTContinueToken : ASTNode {
    override fun visit() = ASTResult.CONTINUE
}

enum class ASTResult {
    BREAK, CONTINUE
}