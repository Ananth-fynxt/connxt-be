plugins {
    id("build.library")
    id("maven-publish")
}

group = "connxt.flow"
version = "0.0.1-SNAPSHOT"

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

dependencies {
    implementation(platform(libs.spring.boot.bom))

    api(project(":libs:shared"))

    implementation(libs.hypersistence)
    implementation(libs.mapstruct)

    implementation(libs.lombok)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    annotationProcessor(libs.mapstruct.processor)
    annotationProcessor(libs.lombokMapstructBinding)
}
