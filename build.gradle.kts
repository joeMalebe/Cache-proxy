plugins {
    kotlin("jvm") version "2.3.0"
    id("com.google.devtools.ksp") version "2.3.0"
    alias(libs.plugins.ktor)
}

group = "com.sbsa.ds.business-lending-digital"
version = "1.0-SNAPSHOT"

application {
    mainClass = "com.cache.proxy.ApplicationKt"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.14.9")

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ktor.client.cio)
    implementation("io.ktor:ktor-client-core-jvm:3.4.1")
    testImplementation(libs.ktor.server.test.host)
    testImplementation("org.mockito.kotlin:mockito-kotlin:6.2.3")
    testImplementation("io.ktor:ktor-client-mock:3.4.1")
    implementation("com.google.dagger:dagger-compiler:2.51.1")
    ksp("com.google.dagger:dagger-compiler:2.51.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}