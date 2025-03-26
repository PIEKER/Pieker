dependencies {
    // Internal
    implementation(project(":common"))
    implementation(project(":generators"))

    // External
    implementation("org.antlr:antlr4-runtime:4.13.2")
    implementation("org.cornutum.regexp:regexp-gen:2.0.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
    implementation("com.github.jsqlparser:jsqlparser:5.1")
    implementation("org.apache.velocity:velocity-engine-core:2.4.1")
    implementation("org.json:json:20250107")
}