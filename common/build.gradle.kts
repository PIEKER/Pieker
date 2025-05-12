plugins {
    java
}

dependencies {
    implementation(project(":api"))
    implementation("org.apache.velocity:velocity-engine-core:2.4.1")
    implementation("org.json:json:20250107")

    testImplementation("org.postgresql:postgresql:42.7.5")
    testImplementation("org.testcontainers:junit-jupiter:1.21.0")
    testImplementation("org.testcontainers:postgresql:1.21.0")
}
