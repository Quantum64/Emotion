package co.q64.emotion.web

import co.q64.emotion.core.extension.emotionCodePointCount
import co.q64.emotion.engine.Emotion
import kotlinx.browser.document
import kotlinx.css.body
import kotlinx.css.height
import kotlinx.css.html
import kotlinx.css.pct
import react.dom.render
import styled.injectGlobal

fun main() {
    val program = """
        load Hello World!
    """.trimIndent()

    val emotion = Emotion(WebConsoleEnvironment)
    val compiled = emotion.compile(program, true)
    println("${compiled.emotionCodePointCount()} bytes: " + compiled)
    println()
    println("Running On Web")
    println("---------------")
    emotion.run(compiled)

    injectGlobal {
        html { height = 100.pct }
        body { height = 100.pct }
        "#root" { height = 100.pct }
    }

    render(document.getElementById("root")) {
        child(Application::class) {}
    }
}