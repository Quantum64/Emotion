import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("multiplatform")
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

kotlin {
    jvm {

    }
    fun org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithHostTests.emotionBinaries() {
        binaries {
            executable("interp") {
                baseName = "emotion"
                entryPoint = "co.q64.emotion.standalone.interp.main"
            }
            executable("compile") {
                baseName = "emotionc"
                entryPoint = "co.q64.emotion.standalone.compile.main"
            }
        }
    }
    mingwX64 {
        emotionBinaries()
    }
    linuxX64 {
        emotionBinaries()
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":emotion-engine"))
                implementation("com.squareup.okio:okio-multiplatform:3.0.0-alpha.1")
            }
        }
    }
}

repositories {
    mavenCentral()
    jcenter()
}

tasks {
    val shadowCreate by creating(ShadowJar::class) {
        manifest {
            attributes["Main-Class"] = "co.q64.emotion.standalone.MainKt"
        }
        archiveClassifier.set("all")
        from(kotlin.jvm().compilations.getByName("main").output)
        configurations =
            mutableListOf(kotlin.jvm().compilations.getByName("main").compileDependencyFiles as Configuration)
    }
    val build by existing {
        dependsOn(shadowCreate)
    }
}