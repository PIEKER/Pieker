plugins {
    java
}

dependencies {
    // Internal
    implementation(project(":api"))
    implementation(project(":common"))

    // External
    implementation("org.antlr:antlr4-runtime:4.13.2")
    implementation("org.cornutum.regexp:regexp-gen:2.0.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.0")
    implementation("com.github.jsqlparser:jsqlparser:5.3")
    implementation("org.apache.velocity:velocity-engine-core:2.4.1")
    implementation("org.apache.commons:commons-jexl3:3.5.0")
    implementation("org.json:json:20250517")
    implementation("info.picocli:picocli:4.7.7")

    testImplementation("org.postgresql:postgresql:42.7.7")
    testImplementation("org.testcontainers:junit-jupiter:1.21.3")
    testImplementation("org.testcontainers:postgresql:1.21.3")
}