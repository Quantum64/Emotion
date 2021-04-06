package co.q64.emotion.core.ast

object ASTEmpty : ASTNode {
    override fun enter() = ASTResult.None
    override fun toString() = "no-op"
}