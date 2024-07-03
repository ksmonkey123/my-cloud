package ch.awae.mycloud.auth

import org.springframework.boot.context.properties.*

@ConfigurationProperties(prefix = "mycloud.security")
data class SecurityProperties (val publicEndpoints: List<String>?)