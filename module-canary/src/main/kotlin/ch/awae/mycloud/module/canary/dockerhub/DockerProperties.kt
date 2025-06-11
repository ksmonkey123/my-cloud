package ch.awae.mycloud.module.canary.dockerhub

import org.springframework.boot.context.properties.*

@ConfigurationProperties(prefix = "canary.dockerhub")
data class DockerProperties(
    val webUrl: String,
    val apiUrl: String,
    val username: String,
    val password: String,
)
