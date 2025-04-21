plugins {
    java
}

dependencies {
    implementation(project(":api"))
    implementation(project(":common"))
    implementation("org.json:json:20250107")
}