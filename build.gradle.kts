plugins {
    java
}

group = "ru.erius.lab5"
version = "2.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.compileJava {
    options.encoding = "windows-1252"
}

tasks.javadoc {
    options.encoding = "UTF-8"
}
