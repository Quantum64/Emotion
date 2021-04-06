package co.q64.emotion.core.ast

object ASTBreak : ASTNode {
    override fun enter() = ASTResult.Break
    override fun toString() = "break"
}