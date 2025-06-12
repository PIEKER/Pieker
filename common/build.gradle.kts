plugins {
    java
}

dependencies {
    implementation(project(":api"))
    implementation("org.apache.velocity:velocity-engine-core:2.4.1")
    implementation("org.json:json:20250517")

    testImplementation("org.postgresql:postgresql:42.7.7")
    testImplementation("org.testcontainers:junit-jupiter:1.21.1")
    testImplementation("org.testcontainers:postgresql:1.21.1")
}
