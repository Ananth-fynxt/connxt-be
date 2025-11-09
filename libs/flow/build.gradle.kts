plugins {
    id("build.library")
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
