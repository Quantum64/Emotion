package co.q64.emotion.extension

import java.io.Serializable

internal object UNINITIALIZED

internal class Remember<out T>(initializer: () -> T) : Lazy<T>, Serializable {
    private var initializer: (() -> T)? = initializer
    private var _value: Any? = UNINITIALIZED

    override val value: T
        get() {
            if (_value === UNINITIALIZED) {
                _value = initializer!!()
                initializer = null
            }
            @Suppress("UNCHECKED_CAST")
            return _value as T
        }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED

    override fun toString(): String = if (isInitialized()) value.toString() else "Lazy value not initialized yet."

    private fun writeReplace(): Any = InitializedRemember(value)
}

internal class InitializedRemember<out T>(override val value: T) : Lazy<T>, Serializable {
    override fun isInitialized(): Boolean = true

    override fun toString(): String = value.toString()
}

fun <T> remember(initializer: () -> T): Lazy<T> = Remember(initializer)