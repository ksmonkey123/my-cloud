package ch.awae.mycloud.module.auth.dto

import ch.awae.mycloud.module.auth.domain.*
import java.time.*

data class ApiKeyDto(
    val name: String,
    val authorities: List<ApiKeyAuthoritiesDto>,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun of(apiKey: ApiKey): ApiKeyDto {
            val activeUserAuthorities = apiKey.owner.roles.filter { it.enabled }.flatMap { it.authorities }.toSet()
            val allUserAuthorities = apiKey.owner.roles.flatMap { it.authorities }.toSet()

            val apiKeyAuthorities = apiKey.authorities.map {
                ApiKeyAuthoritiesDto(
                    name = it,
                    // only if the role is active, we treat it as enabled
                    enabled = activeUserAuthorities.contains(it),
                    // granted means, the auth-code is granted to the user, but may not currently be enabled
                    granted = allUserAuthorities.contains(it),
                )
            }

            return ApiKeyDto(
                name = apiKey.name,
                authorities = apiKeyAuthorities,
                createdAt = apiKey.creationTime,
            )
        }
    }
}

data class ApiKeyAuthoritiesDto(
    val name: String,
    val enabled: Boolean,
    val granted: Boolean,
)