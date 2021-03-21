plugins {
    kotlin("jvm") version "1.4.31"
    `kotlin-dsl`
    `java-library`
}

apply("../dependencies.gradle.kts")

dependencies {
    api("co.q64.emotion:emotion-core:1.0")

    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.31")
}

gradlePlugin {
    plugins.register("emotion") {
        id = "emotion"
        implementationClass = "co.q64.emotion.meta.EmotionPlugin"
    }
}

