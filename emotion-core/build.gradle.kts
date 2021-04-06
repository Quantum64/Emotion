plugins {
    kotlin("multiplatform") version "1.5.0-M2"
}

kotlin {
    jvm()
    js().browser()
    mingwX64()
    linuxX64()

    sourceSets {
        commonMain {
            dependencies {
                api("com.ionspin.kotlin:bignum:0.2.8")
                api("com.soywiz.korlibs.klock:klock:2.0.7")
            }
        }
    }
}

apply("../dependencies.gradle.kts")