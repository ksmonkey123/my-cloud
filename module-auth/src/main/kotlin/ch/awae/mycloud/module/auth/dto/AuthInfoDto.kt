package ch.awae.mycloud.module.auth.dto

import ch.awae.mycloud.api.auth.*

data class AuthInfoDto(
    val type: AuthType,
    val username: String,
    val authorities: List<String>,
    val token: String,
    val languageCode: String,
    val email: String?,
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
                    authInfo.authorities.toList(),
                    authInfo.token,
                    authInfo.language.code,
                    authInfo.email,
                )
            }
        }
    }
}