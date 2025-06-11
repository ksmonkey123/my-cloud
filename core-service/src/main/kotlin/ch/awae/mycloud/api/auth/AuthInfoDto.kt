package ch.awae.mycloud.api.auth

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
                is UserAuthInfo -> AuthInfoDto(
                    AuthType.USER,
                    authInfo.username,
                    authInfo.roles,
                    authInfo.token,
                    authInfo.language.code
                )

                else -> throw IllegalArgumentException("invalid auth type")
            }
        }
    }
}
