plugins {
    java
}

dependencies {
    // Internal
    implementation(project(":architectures"))
    implementation(project(":common"))
    implementation(project(":dsl"))
    //External
    implementation("org.apache.velocity:velocity-engine-core:2.4.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.1")
    implementation("com.github.docker-java:docker-java:3.5.3")
    implementation("com.github.docker-java:docker-java-transport-httpclient5:3.5.3")
}