package co.q64.emotion.extension

import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.Qualifier

val koin: Koin get() = GlobalContext.get().koin

inline fun <reified T> inject(
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
): Lazy<T> =
        remember { koin.get<T>(qualifier, parameters) }

fun parameters(vararg parameters: Any?) = parametersOf(parameters)