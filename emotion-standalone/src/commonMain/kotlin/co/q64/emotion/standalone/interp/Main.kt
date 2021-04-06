package co.q64.emotion.standalone.interp

import co.q64.emotion.engine.Emotion
import co.q64.emotion.standalone.StandaloneEnvironment
import okio.ExperimentalFileSystem
import okio.FileSystem
import okio.Path.Companion.toPath

@ExperimentalFileSystem
fun main(args: Array<String>) {
    if (args.isEmpty()) {
        return println("Specify a file to execute.")
    }
    FileSystem.SYSTEM.read(args.first().toPath()) {
        Emotion(StandaloneEnvironment).run(readUtf8(), args.drop(1).joinToString(" "))
    }
}