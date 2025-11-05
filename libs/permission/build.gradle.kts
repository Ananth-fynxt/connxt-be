plugins {
    id("build.library")
}

dependencies {
    api(project(":libs:shared"))
    implementation(platform(libs.spring.boot.bom))
    api(libs.spring.boot.starter.aop)
    api(libs.aspectj.weaver)
    implementation(libs.lombok)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}
