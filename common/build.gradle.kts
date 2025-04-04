plugins {
    java
}

dependencies {
    implementation("org.apache.velocity:velocity-engine-core:2.4.1")
    implementation("org.json:json:20250107")

    testImplementation("org.postgresql:postgresql:42.7.5")
    testImplementation("org.testcontainers:junit-jupiter:1.20.6")
    testImplementation("org.testcontainers:postgresql:1.20.6")
}
