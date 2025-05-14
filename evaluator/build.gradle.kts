plugins {
    java
}

dependencies {
    implementation(project(":api"))
    implementation(project(":common"))
    implementation("org.json:json:20250107")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
}