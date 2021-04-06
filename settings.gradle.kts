rootProject.name = "emotion"

includeBuild("emotion-core") {
    dependencySubstitution {
        substitute(module("co.q64.emotion:emotion-core")).with(project(":"))
    }
}


includeBuild("emotion-meta")

include(
    "emotion-engine",
    "emotion-standalone",
    "emotion-web"
)