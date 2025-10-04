val kotlin_version: String by project
val ktor_version: String by project
val logback_version: String by project
val mongo_version: String by project
val testcontainers_version: String by project
plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
    id("io.ktor.plugin") version "3.2.3"
    id("com.gradleup.shadow") version "9.0.2"
    id("jacoco")

}

group = "TACS_G6"
version = "0.0.1"

application {
    mainClass.set("com.g7.application.ApplicationKt")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    jvmToolchain(17)  // must match Java toolchain above
}

repositories {
    mavenCentral()
}

jacoco {
    toolVersion = "0.8.10"
}

tasks.test {
    useJUnitPlatform()
    systemProperty("junit.jupiter.execution.parallel.enabled", "false")
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}


dependencies {
    // Ktor server dependencies
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-config-yaml:$ktor_version")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-call-logging-jvm:${ktor_version}")

    // Testing
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktor_version")
    testImplementation("org.testcontainers:testcontainers:$testcontainers_version")
    testImplementation("org.testcontainers:mongodb:$testcontainers_version")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainers_version")

    // Json
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("io.ktor:ktor-server-content-negotiation:${ktor_version}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${ktor_version}")

    // Encryption
    implementation("org.mindrot:jbcrypt:0.4")

    // CORS
    implementation("io.ktor:ktor-server-cors:${ktor_version}")

    // Status Pages
    implementation("io.ktor:ktor-server-status-pages:${ktor_version}")


    // Auth
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("io.ktor:ktor-server-auth:${ktor_version}")
    implementation("io.ktor:ktor-server-auth-jwt:${ktor_version}")

    // Mongo
    implementation("org.mongodb:mongodb-driver-core:${mongo_version}")
    implementation("org.mongodb:mongodb-driver-sync:${mongo_version}")
    implementation("org.mongodb:bson:${mongo_version}")
    implementation("org.mongodb:bson-kotlin:${mongo_version}")

}