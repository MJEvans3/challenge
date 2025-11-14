import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer

plugins {
    java
    application
    id("com.gradleup.shadow") version "9.2.0"
}

group = "org.light"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

dependencies {
    // Dropwizard
    implementation("io.dropwizard:dropwizard-core:2.1.4")
    
    // Jackson for JSON
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4")
    
    // Logging
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.8.0")
}

application {
    mainClass.set("org.light.challenge.App")
}

tasks.named<JavaExec>("run") {
    args = listOf("server", "config.yml")
}

tasks.withType<Test> {
    useJUnit()
}

tasks.register<JavaExec>("runTestRunner") {
    group = "application"
    description = "Runs the WorkflowTestRunner class"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("org.light.challenge.WorkflowTestRunner")
}

tasks.shadowJar {
    mergeServiceFiles()
    transform(ServiceFileTransformer::class.java)
    manifest {
        attributes["Main-Class"] = "org.light.challenge.App"
    }
    archiveClassifier.set("")
}




