plugins {
    java
    id("com.github.johnrengelman.shadow") version("7.1.2")
}

group = "ru.erius.lab5"
version = "3.0"
val mainClass = "server.Lab5Server"
val psqlJar = "postgresql-42.4.0.jar"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    compileOnly(files("$buildDir/libs/$psqlJar"))
    implementation(project(":Lab5Core"))

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
    exclude(psqlJar)
    archiveClassifier.set("")
    manifest {
        attributes(
            "Manifest-Version" to "1.0",
            "Main-Class" to mainClass,
            "Class-Path" to psqlJar
        )
    }
}
