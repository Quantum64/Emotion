package co.q64.emotion.core.ast

data class AST(
    private val nodes: List<ASTNode>
) : ASTNode {

    override fun enter(): ASTResult {
        nodes.forEach { node ->
            when (val result = node.enter()) {
                ASTResult.Break, ASTResult.Continue -> return result
            }
        }
        return ASTResult.None
    }

    override fun toString() =
        "[${nodes.joinToString()}]"
}
