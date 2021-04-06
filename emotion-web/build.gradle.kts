plugins {
    kotlin("js")
}

dependencies {
    implementation(project(":emotion-engine"))
    implementation("net.subroh0508.kotlinmaterialui:core:0.5.5")
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