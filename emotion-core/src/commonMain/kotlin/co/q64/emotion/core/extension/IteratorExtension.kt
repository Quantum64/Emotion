package co.q64.emotion.core.extension

fun <T> Iterator<T>.nextOrNull() = takeIf { hasNext() }?.next()