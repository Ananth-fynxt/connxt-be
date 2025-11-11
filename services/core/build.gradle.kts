plugins {
    id("build.service")
}

repositories {
    mavenLocal()
}

dependencies {
    // Spring Boot BOM for version management
    implementation(platform(libs.spring.boot.bom))
    
    // Spring Boot starters (versions managed by BOM)
    implementation(libs.bundles.spring.boot.web)
    implementation(libs.bundles.database.jdbc)
    implementation(libs.hypersistence)
    
    // Development Tools
    implementation(libs.lombok)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    
    // Caching
    implementation(libs.bundles.caching)
    
    // Authentication
    
    // Project modules
    implementation(project(":libs:shared"))
    implementation(project(":libs:denovm"))
    implementation(project(":libs:email"))
    implementation(project(":libs:jwt"))

    implementation("connxt.flow:flow:0.0.1-SNAPSHOT")

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    annotationProcessor(libs.lombokMapstructBinding)
}
