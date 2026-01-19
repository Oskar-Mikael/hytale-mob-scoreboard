plugins {
    java
}

group = "com.oskarmikael.mobkilltracker"
version = "1.0.0"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

dependencies {
    // Hytale Server API - compile only since it's provided at runtime
    compileOnly(files("../server/Server/HytaleServer.jar"))

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains:annotations:24.1.0")

    // Testing
    testImplementation(libs.junit)
}

tasks.jar {
    // Set the archive name
    archiveBaseName.set("MobKilltracker")
}
