plugins {
    id("build.library")
}

dependencies {
    // Spring Boot BOM for version management
    implementation(platform(libs.spring.boot.bom))
    
    // Spring Boot starters (versions managed by BOM)
    api(libs.spring.boot.starter.web)
    api(libs.spring.boot.starter.data.jpa)
    api(libs.spring.boot.starter.jdbc)
    api(libs.spring.boot.starter.security)
    api(libs.spring.boot.starter.validation)
    
    // Database
    api(libs.postgresql)
    api(libs.hikaricp)
    api(libs.hibernate.envers)
    
    // JSON Schema Validation
    api(libs.json.schema.validator)
    
    // Jackson for JSON processing (versions managed by BOM)
    api(libs.jackson.databind)
    api(libs.jackson.datatype.jsr310)
    api(libs.mapstruct)
    
    // Development Tools
    implementation(libs.lombok)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    
    // Swagger/OpenAPI Documentation
    api(libs.spring.doc.openapi.starter.webmvc.ui)
    
    // Background Job Processing
    api(libs.jobrunr.spring.boot.starter)
    
    // Email Service
    api(libs.azure.communication.services.email)

    // Rule Engine
    implementation(libs.json.logic)
    
    // JWT Processing

    // Netty DNS Resolver for macOS - fixes DNS resolution issues on macOS
    // Spring Boot 3.5.4 uses Netty 4.1.114.Final
    // For Apple Silicon Macs (M1/M2/M3):
    runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.114.Final:osx-aarch_64")
    // For Intel Macs, use instead:
    // runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.114.Final:osx-x86_64")
}
