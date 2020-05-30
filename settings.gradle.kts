rootProject.name = "eureka-service"

pluginManagement {
    val springBootPluginVersion: String by settings
    val springDependencyManagementPluginVersion: String by settings
    val dockerRemoteApiVersion: String by settings

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "org.springframework.boot" -> useVersion(springBootPluginVersion)
                "io.spring.dependency-management" -> useVersion(springDependencyManagementPluginVersion)
                "com.bmuschko.docker-remote-api" -> useVersion(dockerRemoteApiVersion)
            }
        }
    }
}