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
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
    implementation("com.github.jsqlparser:jsqlparser:5.1")
    implementation("org.apache.velocity:velocity-engine-core:2.4.1")
    implementation("org.apache.commons:commons-jexl3:3.2.1")
    implementation("org.json:json:20250107")
    implementation("info.picocli:picocli:4.7.6")

    testImplementation("org.postgresql:postgresql:42.7.5")
    testImplementation("org.testcontainers:junit-jupiter:1.21.0")
    testImplementation("org.testcontainers:postgresql:1.21.0")
}