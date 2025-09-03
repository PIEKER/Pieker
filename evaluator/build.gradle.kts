plugins {
    java
}

dependencies {
    implementation(project(":api"))
    implementation(project(":architectures"))
    implementation(project(":common"))
    implementation("org.json:json:20250517")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.0")
}