plugins {
    kotlin("js")
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation(project(":emotion-engine"))
    implementation("org.jetbrains:kotlin-react:pre.148-kotlin-1.4.30")
    implementation("org.jetbrains", "kotlin-styled", "5.2.1-pre.148-kotlin-1.4.30")
    implementation("net.subroh0508.kotlinmaterialui:core:0.5.5")
    implementation(npm("@monaco-editor/react", "4.1.1"))
}

repositories {
    jcenter()
    maven("https://dl.bintray.com/subroh0508/maven")
}

kotlin {
    js {
        binaries.executable()
        browser {
            webpackTask {
                cssSupport.enabled = true
            }

            runTask {
                cssSupport.enabled = true
            }

            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
    }
}