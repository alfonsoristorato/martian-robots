plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.gradle.ktlint)
    alias(libs.plugins.kover)
    alias(libs.plugins.kotest.gradle.plugin)
}

group = "martianrobots"

dependencies {
    testImplementation(libs.bundles.kotest)
}

tasks.test {
    useJUnitPlatform()
    failOnNoDiscoveredTests = false
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
