import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.Dockerfile
import com.bmuschko.gradle.docker.tasks.container.*
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val springCloudVersion: String by project

val build: DefaultTask by tasks
val shadowJar: DefaultTask by tasks

repositories {
    mavenCentral()
}

plugins {
    java
    application
    idea
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.bmuschko.docker-remote-api")
    id("com.github.johnrengelman.shadow") version "5.1.0"
}

application {
    applicationName = "eureka-service"
    mainClassName = "com.goal.eureka.EurekaServerApp"
}

group = "eureka-service"
version = "1.0-SNAPSHOT"

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")
    implementation("org.springframework.cloud:spring-cloud-config-client")
    implementation("org.springframework.boot:spring-boot-starter-web")
}


configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<ShadowJar>() {
    manifest {
        attributes["Main-Class"] = application.mainClassName
    }
}

tasks.register<Copy>("copyJar") {
    from("$buildDir/libs")
    include("*.jar")
    into("$buildDir/docker")
}

val createDockerfile by tasks.creating(Dockerfile::class) {
    from("openjdk:11-slim")
    copyFile("eureka-service-1.0-SNAPSHOT.jar", "eureka-service.jar")
    entryPoint("java","-jar","/eureka-service.jar")
    exposePort(8761)
}

val buildMyAppImage by tasks.creating(DockerBuildImage::class) {
    dependsOn(createDockerfile)
    images.add("eureka-service:latest")
}

val createMyAppContainer by tasks.creating(DockerCreateContainer::class) {
    dependsOn(buildMyAppImage)
    targetImageId(buildMyAppImage.getImageId())
    hostConfig.portBindings.set(listOf("8761:8761"))
    hostConfig.autoRemove.set(true)
}


val startMyAppContainer by tasks.creating(DockerStartContainer::class) {
    dependsOn(createMyAppContainer)
    targetContainerId(createMyAppContainer.getContainerId())
}

task("startContainer"){
    dependsOn(build, "copyJar", startMyAppContainer)
}

task("stage") {
    dependsOn(build,  shadowJar, buildMyAppImage)
}


