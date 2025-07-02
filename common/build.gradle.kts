plugins {
    java
}

dependencies {
    implementation(project(":api"))
    implementation("org.apache.velocity:velocity-engine-core:2.4.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.13.1")

    testImplementation("org.postgresql:postgresql:42.7.7")
    testImplementation("org.testcontainers:junit-jupiter:1.21.3")
    testImplementation("org.testcontainers:postgresql:1.21.3")
}
