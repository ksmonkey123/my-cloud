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
            val userAuthorities = apiKey.owner.roles.flatMap { it.authorities }.toSet()

            val apiKeyAuthorities = apiKey.authorities.map {
                ApiKeyAuthoritiesDto(
                    name = it,
                    // only if the role is active, we treat it as enabled
                    active = userAuthorities.contains(it),
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
    val active: Boolean,
)