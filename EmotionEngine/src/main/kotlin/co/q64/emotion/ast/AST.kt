package co.q64.emotion.ast

import co.q64.emotion.lang.Comparison
import co.q64.emotion.lang.value.Value

interface ASTNode {
    fun visit(): ASTResult?
}

class AST(private val nodes: List<ASTNode>) : ASTNode {
    override fun visit(): ASTResult? {
        nodes.forEach { node -> node.visit()?.let { return it } }
        return null
    }

    override fun toString(): String = "[${nodes.joinToString()}]"
}

class ASTBreakToken : ASTNode {
    override fun visit(): ASTResult? = ASTResult.BREAK
}

class ASTContinueToken : ASTNode {
    override fun visit(): ASTResult? = ASTResult.CONTINUE
}

class ASTConditional(private val pass: AST, private val fail: AST, private val type: Comparison, private val push: Value? = null) : ASTNode {
    override fun visit(): ASTResult? {
        when (type) {

        }
        return null
    }
}

enum class ASTResult {
    BREAK, CONTINUE
}