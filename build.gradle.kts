plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.gradle.ktlint)
    alias(libs.plugins.kover)
    alias(libs.plugins.kotest.gradle.plugin)
}

group = "martianrobots"

application {
    mainClass.set("martianrobots.MainKt")
}

tasks.named<JavaExec>("run") {
    // Gradle does not forward stdin to `run` by default
    standardInput = System.`in`
}

dependencies {
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.kotlin.reflect)
}

tasks.test {
    useJUnitPlatform()
}

tasks.build {
    dependsOn(tasks.ktlintFormat, tasks.koverVerify)
}

kotlin {
    compilerOptions {
        extraWarnings.set(true)
        allWarningsAsErrors.set(true)
    }
    jvmToolchain(25)
}

kover {
    reports {
        verify {
            rule {
                minBound(80)
            }
        }
    }
}
