// Pure Kotlin/JVM module: domain, repositories, ports. No Android dependencies,
// so its tests run with `./gradlew :core:test` on any machine with a JDK.
plugins {
    id("org.jetbrains.kotlin.jvm")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
