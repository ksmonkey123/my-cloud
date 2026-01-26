package ch.awae.mycloud.module.auth.dto

import ch.awae.mycloud.api.auth.ApiKeyUserAuthInfo
import ch.awae.mycloud.api.auth.AuthInfo
import ch.awae.mycloud.api.auth.BearerTokenUserAuthInfo

data class AuthInfoDto(
    val type: AuthType,
    val username: String,
    val authorities: List<String>,
    val token: String,
    val languageCode: String,
    val email: String?,
) {

    enum class AuthType {
        USER,
        API_KEY,
    }

    companion object {
        fun of(authInfo: AuthInfo): AuthInfoDto {
            return when (authInfo) {
                is BearerTokenUserAuthInfo -> AuthInfoDto(
                    AuthType.USER,
                    authInfo.username,
                    authInfo.authorities.toList(),
                    authInfo.token,
                    authInfo.language.code,
                    authInfo.email,
                )

                is ApiKeyUserAuthInfo -> AuthInfoDto(
                    AuthType.API_KEY,
                    authInfo.username,
                    authInfo.authorities.toList(),
                    authInfo.token,
                    authInfo.language.code,
                    authInfo.email,
                )
            }
        }
    }
}