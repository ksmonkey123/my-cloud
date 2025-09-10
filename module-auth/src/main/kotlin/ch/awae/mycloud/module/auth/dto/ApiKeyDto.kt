package ch.awae.mycloud.module.auth.dto

import ch.awae.mycloud.module.auth.domain.*
import java.time.*

data class ApiKeyDto(
    val name: String,
    val roles: List<ApiKeyRoleDto>,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun of(apiKey: ApiKey): ApiKeyDto {
            val userRoleNames = apiKey.owner.roles.map { it.name }
            val apiKeyRoles = apiKey.roles.map {
                ApiKeyRoleDto(
                    name = it.name,
                    description = it.description,
                    enabled = it.enabled,
                    granted = userRoleNames.contains(it.name),
                )
            }

            return ApiKeyDto(
                name = apiKey.name,
                roles = apiKeyRoles,
                createdAt = apiKey.creationTime,
            )
        }
    }
}

data class ApiKeyRoleDto(
    val name: String,
    val description: String?,
    val enabled: Boolean,
    val granted: Boolean,
)