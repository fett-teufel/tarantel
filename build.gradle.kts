import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

group = "dev.roteblume"
version = "0.1-SNAPSHOT"

val kotlinVersion: String by project
val vertxVersion: String by project



java.sourceCompatibility = JavaVersion.VERSION_1_8

plugins {
    kotlin("jvm") version "1.3.21"
    id("io.gitlab.arturbosch.detekt").version("1.0.0-RC16")
    idea
}

allprojects {
    repositories {
        jcenter()
        maven("https://art.roteblume.dev/repository/roteblume/")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks {
    // Use the built-in JUnit support of Gradle.
    "test"(Test::class) {
        useJUnitPlatform()
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    compile("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    compile("io.vertx:vertx-lang-kotlin:$vertxVersion")
    compile("io.vertx:vertx-core:$vertxVersion")
    compile("io.vertx:vertx-lang-kotlin-coroutines:$vertxVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.2.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.2.0")
    testRuntime("org.junit.platform:junit-platform-console:1.2.0")
    testCompile("org.mockito:mockito-core:2+")
    testCompile("org.mockito:mockito-junit-jupiter:2.18.3")
    testCompile("com.nhaarman.mockitokotlin2:mockito-kotlin:2.1.0")
    testCompile("io.vertx:vertx-unit:$vertxVersion")
    testCompile("io.vertx:vertx-junit5:$vertxVersion")
    testCompile("dev.roteblume:kottbus:0.1")
    testCompile("org.msgpack:msgpack-core:0.8.18")
}

repositories {
    mavenCentral()
    maven("https://art.roteblume.dev/repository/roteblume/")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
