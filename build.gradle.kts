import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import io.gitlab.arturbosch.detekt.Detekt

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

plugins {
    // Kotlin Plugins
    kotlin("jvm") version "1.9.21"
    id("io.gitlab.arturbosch.detekt") version "1.23.4"
    application

    // IDE
    idea

    // Executable JAR
    id("com.github.johnrengelman.shadow") version "8.1.1"

    // Code compliance
    id("com.diffplug.spotless") version "6.23.0"

    // Dependencies (Java, Kotlin) & Plugins versions
    id("com.github.ben-manes.versions") version "0.50.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

defaultTasks = mutableListOf("shadowJar")

val mainClassName = "dev.siller.aoc2023.RunnerKt"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.13")
    implementation("org.fusesource.jansi:jansi:2.4.1")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

spotless {
    kotlin {
        endWithNewline()
        trimTrailingWhitespace()
        ktlint()
    }

    kotlinGradle {
        target("*.gradle.kts")

        endWithNewline()
        trimTrailingWhitespace()
        ktlint()
    }
}

application {
    mainClass.set(mainClassName)
}

tasks.withType<Jar> {
    manifest {
        attributes("Main-Class" to mainClassName)
    }
}

tasks.withType<DependencyUpdatesTask> {
    revision = "release"
    gradleReleaseChannel = "release"
    checkForGradleUpdate = true
    outputFormatter = "plain"
    outputDir = "build/dependencyUpdates"
    reportfileName = "report"

    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}

tasks.withType<Detekt> {
    jvmTarget = "21"
    ignoreFailures = true
    config.setFrom(file("detekt.yml"))
    buildUponDefaultConfig = true

    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(true)
        sarif.required.set(false)
    }
}
