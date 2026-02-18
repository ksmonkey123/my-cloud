package ch.awae.mycloud.auth.dto

import ch.awae.mycloud.auth.ApiKeyUserAuthInfo
import ch.awae.mycloud.auth.AuthInfo
import ch.awae.mycloud.auth.BearerTokenUserAuthInfo

data class AuthInfoDto(
    val type: AuthType,
    val username: String,
    val authorities: List<String>,
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
                    authInfo.language.code,
                    authInfo.email,
                )

                is ApiKeyUserAuthInfo -> AuthInfoDto(
                    AuthType.API_KEY,
                    authInfo.username,
                    authInfo.authorities.toList(),
                    authInfo.language.code,
                    authInfo.email,
                )
            }
        }
    }
}