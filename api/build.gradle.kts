plugins {
    java
}

group = "pieker-api"
version = "0.1-SNAPSHOT"

dependencies {
    implementation("org.apache.commons:commons-jexl3:3.2.1")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}