plugins {
    java
}

dependencies {
    // Internal
    implementation(project(":common"))
    testImplementation(project(":dsl"))
    // External
    implementation("org.yaml:snakeyaml:2.4")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.20.0")
}