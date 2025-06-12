package ch.awae.mycloud.module.auth.dto

import ch.awae.mycloud.api.auth.AuthInfo
import ch.awae.mycloud.api.auth.BearerTokenUserAuthInfo

data class AuthInfoDto(
    val type: AuthType,
    val username: String,
    val roles: List<String>,
    val token: String,
    val languageCode: String,
) {

    enum class AuthType {
        USER
    }

    companion object {
        fun of(authInfo: AuthInfo): AuthInfoDto {
            return when (authInfo) {
                is BearerTokenUserAuthInfo -> AuthInfoDto(
                    AuthType.USER,
                    authInfo.username,
                    authInfo.roles,
                    authInfo.token,
                    authInfo.language.code
                )

                else -> throw kotlin.IllegalArgumentException("invalid auth type")
            }
        }
    }
}