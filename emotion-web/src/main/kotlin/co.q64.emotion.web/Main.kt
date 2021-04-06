package co.q64.emotion.web

import co.q64.emotion.core.extension.emotionCodePointCount
import co.q64.emotion.engine.Emotion

fun main() {
    val program = """
        load Hello World!
    """.trimIndent()

    val emotion = Emotion(WebConsoleEnvironment)
    val compiled = emotion.compile(program, true)
    println("${compiled.emotionCodePointCount(0, compiled.length)} bytes: " + compiled)
    println()
    println("Running On Web")
    println("---------------")
    emotion.run(compiled)
}