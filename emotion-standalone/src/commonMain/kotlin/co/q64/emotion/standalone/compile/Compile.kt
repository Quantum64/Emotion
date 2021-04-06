package co.q64.emotion.standalone.compile

import co.q64.emotion.engine.Emotion
import co.q64.emotion.standalone.StandaloneEnvironment
import okio.ExperimentalFileSystem
import okio.FileSystem
import okio.Path.Companion.toPath

@ExperimentalFileSystem
fun main(args: Array<String>) {
    if (args.isEmpty()) {
        return println("Specify a file to compile.")
    }
    FileSystem.SYSTEM.read(args.first().toPath()) {
        Emotion(StandaloneEnvironment).compile(readUtf8())
    }
}