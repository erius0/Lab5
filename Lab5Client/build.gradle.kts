plugins {
    java
    id("com.github.johnrengelman.shadow") version("7.1.2")
}

group = "ru.erius.lab5"
version = "3.0"
val mainClass = "client.Lab5Client"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    implementation(project(":Lab5Core"))
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.compileJava {
    options.encoding = "UTF-8"
}

tasks.javadoc {
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    archiveClassifier.set("")
    manifest {
        attributes(
            "Manifest-Version" to "1.0",
            "Main-Class" to mainClass
        )
    }
}
