plugins {
    java
}

group = "ru.erius"
version = "1.2"
val mainClass = "$group.${name.toLowerCase()}.$name"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    compileOnly("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")
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

tasks.jar {
    manifest {
        attributes(
            "Manifest-Version" to "1.0",
            "Main-Class" to mainClass
        )
    }
}
