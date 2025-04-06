plugins {
    java
    application
}

dependencies {
    // Internal
    implementation(project(":dsl"))
    implementation(project(":common"))
    implementation(project(":generators"))
    // External

}

application {
    mainClass.set("pieker.app.Main")
}
