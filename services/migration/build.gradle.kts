plugins {
    id("build.migration")
}

application {
    mainClass.set("nexxus.migration.DatabaseMigration")
}

dependencies {
    // Flyway dependencies
    implementation(libs.flyway.core)
    implementation(libs.flyway.database.postgresql)
    
    // PostgreSQL driver
    implementation(libs.postgresql)
}
