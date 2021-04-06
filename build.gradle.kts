plugins {
    kotlin("multiplatform") version "1.5.0-M2"
    id("emotion")
}

kotlin {
    jvm()
}

apply("dependencies.gradle.kts")