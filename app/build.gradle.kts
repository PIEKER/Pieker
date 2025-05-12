plugins {
    java
    application
}

dependencies {
    // Internal
    implementation(project(":api"))
    implementation(project(":architectures"))
    implementation(project(":common"))
    implementation(project(":dsl"))
    implementation(project(":evaluator"))
    implementation(project(":generators"))
    implementation(project(":supervisor"))
    // External

}

application {
    mainClass.set("pieker.app.Main")
}
