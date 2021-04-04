package co.q64.emotion.engine.compiler

data class CompiledInstruction(
    val instruction: String,
    val description: String,
    val encoded: String,
    val line: Int
)