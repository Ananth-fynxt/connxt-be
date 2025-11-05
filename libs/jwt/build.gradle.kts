plugins {
    id("build.library")
}

dependencies {
    api(project(":libs:shared"))
    
    implementation(platform(libs.spring.boot.bom))
    api(libs.spring.boot.starter.aop)
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)
    implementation(libs.lombok)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}
