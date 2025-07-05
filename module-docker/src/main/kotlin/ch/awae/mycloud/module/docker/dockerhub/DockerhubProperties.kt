package ch.awae.mycloud.module.docker.dockerhub

import org.springframework.boot.context.properties.*

@ConfigurationProperties(prefix = "docker.dockerhub")
data class DockerhubProperties(
    val webUrl: String,
    val apiUrl: String,
    val username: String,
    val password: String,
)
