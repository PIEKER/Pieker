plugins {
    java
}

dependencies {
    // Internal
    implementation(project(":common"))
    testImplementation(project(":dsl"))
    // External
    implementation("org.yaml:snakeyaml:2.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.18.3")
}