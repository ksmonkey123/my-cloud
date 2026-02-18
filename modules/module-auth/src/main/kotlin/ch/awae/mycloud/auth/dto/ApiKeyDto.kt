package ch.awae.mycloud.auth.dto

import ch.awae.mycloud.auth.domain.ApiKey
import java.time.LocalDateTime

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
            }.sortedBy { it.name }

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