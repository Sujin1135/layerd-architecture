plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "data"
include("api")
include("domain")
include("infrastructure")
include("boot")
