package ch.awae.mycloud.api.auth

import org.springframework.security.authentication.*
import org.springframework.security.core.*
import org.springframework.security.core.authority.*
import org.springframework.security.core.context.*

sealed interface AuthInfo {
    val username: String
    val roles: List<String>
    val language: Language
    val email: String?

    fun toAuthentication(): Authentication {
        return UsernamePasswordAuthenticationToken(
            this.username,
            this,
            this.roles.map(::SimpleGrantedAuthority)
        )
    }

    companion object {

        val info: AuthInfo
            get() = infoOrNull ?: throw NullPointerException("no auth info present in current context")

        val infoOrNull: AuthInfo?
            get() = SecurityContextHolder.getContext().authentication?.credentials as? AuthInfo

        val username: String
            get() = info.username

        val language: Language
            get() = info.language

        val email: String?
            get() = info.email

        inline fun <T> impersonate(username: String, action: () -> T): T {
            val ctx = SecurityContextHolder.getContext()
            val backupCtx = ctx.authentication

            try {
                ctx.authentication = BasicImpersonation(username).toAuthentication()
                return action()
            } finally {
                ctx.authentication = backupCtx
            }
        }
    }
}

data class BearerTokenUserAuthInfo(
    override val username: String,
    override val roles: List<String>,
    val token: String,
    override val language: Language,
    override val email: String?,
) : AuthInfo

data class BasicImpersonation(
    override val username: String
) : AuthInfo {
    override val roles: List<String>
        get() = emptyList()
    override val language: Language
        get() = Language.ENGLISH
    override val email: String?
        get() = null
}