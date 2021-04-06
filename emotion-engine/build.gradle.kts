plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    js().browser()
    mingwX64()
    linuxX64()
    sourceSets {
        commonMain {
            kotlin.srcDir("../build/generated/src/main/kotlin")
            println(kotlin.srcDirs)
            dependencies {
                api("co.q64.emotion:emotion-core:1.0")
            }
        }
    }
    sourceSets["jvmTest"].dependencies {
        implementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
        implementation("org.junit.jupiter:junit-jupiter-engine:5.7.1")
        implementation("io.kotest:kotest-assertions-jvm:4.0.7")
    }
}


tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
}
