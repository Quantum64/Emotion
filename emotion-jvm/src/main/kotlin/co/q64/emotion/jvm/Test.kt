package co.q64.emotion.jvm

import co.q64.emotion.engine.Emotion

fun main() {
    val program = """
        load 2
        stop.print
    """.trimIndent()

    val emotion = Emotion(JvmEnvironment)
    val compiled = emotion.compile(program)
    println(compiled)
    println()
    println("Running")
    println("---------------")
    emotion.run(compiled)
}