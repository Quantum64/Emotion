@file:JsModule("@monaco-editor/react")
@file:JsNonModule

package co.q64.emotion.web.external

import react.RClass
import react.RProps

@JsName("default")
external val editor: RClass<EditorProps>

external interface EditorProps : RProps {
    var value: String
    var options: dynamic
    var onChange: (String, dynamic) -> Unit
}