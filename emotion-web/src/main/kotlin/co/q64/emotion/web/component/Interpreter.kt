package co.q64.emotion.web.component

import co.q64.emotion.core.extension.emotionCodePointCount
import co.q64.emotion.web.external.editor
import kotlinx.css.height
import kotlinx.css.pct
import materialui.components.grid.enums.GridDirection.column
import materialui.components.grid.grid
import materialui.components.typography.enums.TypographyVariant.h6
import materialui.components.typography.typography
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.setState
import styled.inlineStyles
import kotlin.js.json

class Interpreter : RComponent<RProps, InterpreterState>() {
    override fun RBuilder.render() {
        grid {
            attrs { container = true; direction = column }
            inlineStyles { height = 100.pct }
            grid {
                attrs { item = true }
                typography {
                    attrs { variant = h6 }
                    +"Code (${state.code.emotionCodePointCount()} bytes)"
                }
            }
            grid {
                attrs { item = true; xs(true) }
                editor {
                    attrs {
                        value = state.code
                        options = json(
                            "automaticLayout" to true
                        )
                        onChange = { value: String, _: dynamic ->
                            setState { code = value }
                        }
                    }
                }
            }
            grid {
                attrs { item = true }
                typography {
                    attrs { variant = h6 }
                    +"Arguments"
                }
            }
        }
    }

    override fun InterpreterState.init() {
        code = ""
        arguments = ""
        output = ""
    }
}

external interface InterpreterState : RState {
    var code: String
    var arguments: String
    var output: String
}