package co.q64.emotion.test

import co.q64.emotion.core.runtime.Environment
import co.q64.emotion.core.runtime.Output
import co.q64.emotion.engine.Emotion

private object TestEnvironment : Environment, Output {
    override val output get() = this
    override fun print(message: String) = kotlin.io.print(message)
    override fun println(message: String) = kotlin.io.println(message)
}

fun main() {
    val emotion = Emotion(TestEnvironment)
    println(emotion.compile("load 435768297834"))
}