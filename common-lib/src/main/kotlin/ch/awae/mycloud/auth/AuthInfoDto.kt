package ch.awae.mycloud.auth

data class AuthInfoDto(
    val type: AuthType,
    val username: String,
    val roles: List<String>,
    val token: String,
) {

    enum class AuthType {
        USER
    }

    companion object {
        fun of(authInfo: AuthInfo): AuthInfoDto {
            return when (authInfo) {
                is UserAuthInfo -> AuthInfoDto(AuthType.USER, authInfo.username, authInfo.roles, authInfo.token)
                else -> throw IllegalArgumentException("invalid auth type")
            }
        }
    }
}
