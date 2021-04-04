package co.q64.emotion.jvm

import co.q64.emotion.engine.Emotion

fun main() {
    val program = """
        load 2
        load 3
        num.concat
    """.trimIndent()

    val emotion = Emotion(JvmEnvironment)
    val compiled = emotion.compile(program, true)
    println("${compiled.codePointCount(0, compiled.length)} bytes: " + compiled)
    println()
    println("Running")
    println("---------------")
    emotion.run(compiled)
}