plugins {
    kotlin("jvm")
}

dependencies {
    api("co.q64.emotion:emotion-core:1.0")

    testImplementation(platform("org.junit:junit-bom:5.7.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.kotest:kotest-assertions-jvm:4.0.7")
}

kotlin.sourceSets.getByName("main") {
    kotlin.srcDir("../build/generated/src/main/kotlin")
}

tasks {
    test {
        useJUnitPlatform()
    }
}