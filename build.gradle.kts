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
        toolchain {
            languageVersion = JavaLanguageVersion.of(25)
        }
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }

    dependencies {
        // Common-Dependencies
        implementation("org.slf4j:slf4j-api:2.0.17")
        implementation("ch.qos.logback:logback-classic:1.5.32")
        compileOnly("org.projectlombok:lombok:1.18.46")
        annotationProcessor("org.projectlombok:lombok:1.18.46")

        // Test-Dependencies
        testImplementation(platform("org.junit:junit-bom:6.0.3"))
        testImplementation("org.junit.jupiter:junit-jupiter:6.0.3")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.0.3")
        testCompileOnly("org.projectlombok:lombok:1.18.46")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.46")
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
