plugins {
    java
    base
}

allprojects {
    group = "pieker"
    version = "0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    java {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
    }

    dependencies {
        // Common-Dependencies
        implementation("org.slf4j:slf4j-api:2.0.17")
        implementation("ch.qos.logback:logback-classic:1.5.18")
        compileOnly("org.projectlombok:lombok:1.18.38")
        annotationProcessor("org.projectlombok:lombok:1.18.38")

        // Test-Dependencies
        testImplementation(platform("org.junit:junit-bom:5.13.3"))
        testImplementation("org.junit.jupiter:junit-jupiter:5.13.3")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.13.3")
        testCompileOnly("org.projectlombok:lombok:1.18.38")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.38")
    }

    tasks.processResources {
        from(rootProject.file(".config")) {
            include("logback.xml")
        }
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        project.properties.forEach { (key, value) ->
            systemProperty(key, value.toString())
        }
        systemProperty(
            "projectRoot",
            project.projectDir.absolutePath.toString() + File.pathSeparator + ".." + File.pathSeparator
        )
    }

    tasks.withType<JavaExec> {
        classpath = sourceSets.main.get().runtimeClasspath
        systemProperty("logback.configurationFile", ".config/logback.xml")
        project.properties.forEach { (k, v) ->
            if (v != null) {
                systemProperty(k, v.toString())
            }
        }
        systemProperty("projectRoot", project.projectDir.absolutePath.toString() + "/../")
    }

}
