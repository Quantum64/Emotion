package co.q64.emotion.standalone

import co.q64.emotion.core.runtime.Environment
import co.q64.emotion.core.runtime.Output

object StandaloneEnvironment : Environment, Output {
    override val output get() = this
    override fun print(message: String) = kotlin.io.print(message)
    override fun println(message: String) = kotlin.io.println(message)
}