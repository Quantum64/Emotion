package co.q64.emotion.core.ast

object ASTContinue : ASTNode {
    override fun enter() = ASTResult.Continue
    override fun toString() = "continue"
}