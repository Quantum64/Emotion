package co.q64.emotion.web

import co.q64.emotion.web.component.Interpreter
import materialui.components.cssbaseline.cssBaseline
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState

class Application : RComponent<RProps, RState>() {
    override fun RBuilder.render() {
        cssBaseline { }
        child(Interpreter::class) {}
    }
}

data class ApplicationState(
    val interpreterCode: String,
) : RState {
}