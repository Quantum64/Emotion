package co.q64.emotion.meta

import org.gradle.api.Plugin
import org.gradle.api.Project

class EmotionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.getByName("build").dependsOn += "emotion"

        target.tasks.create("emotion") {
            doLast {
                processOpcodes()
            }
        }
    }
}