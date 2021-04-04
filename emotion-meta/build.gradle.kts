plugins {
    kotlin("jvm") version "1.5.0-M2"
    `kotlin-dsl`
    `java-library`
}

apply("../dependencies.gradle.kts")

dependencies {
    api("co.q64.emotion:emotion-core:1.0")

    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.0-M2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.0-M2")
    implementation("com.squareup:kotlinpoet:1.7.2")
}

gradlePlugin {
    plugins.register("emotion") {
        id = "emotion"
        implementationClass = "co.q64.emotion.meta.EmotionPlugin"
    }
}

