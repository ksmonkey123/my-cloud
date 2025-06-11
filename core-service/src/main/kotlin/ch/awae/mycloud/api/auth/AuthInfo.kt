package ch.awae.mycloud.api.auth

import org.springframework.security.authentication.*
import org.springframework.security.core.*
import org.springframework.security.core.authority.*
import org.springframework.security.core.context.*

sealed interface AuthInfo {
    val username: String
    val roles: List<String>
    val language: Language

    fun toAuthentication(): Authentication {
        return UsernamePasswordAuthenticationToken(
            this.username,
            this,
            this.roles.map(::SimpleGrantedAuthority)
        )
    }

    companion object {

        val info: AuthInfo?
            get() = SecurityContextHolder.getContext().authentication?.credentials as? AuthInfo

        val username: String?
            get() = info?.username

        val language: Language?
            get() = info?.language

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

sealed interface TokenBackedAuthInfo : AuthInfo {
    val token: String
}

data class UserAuthInfo(
    override val username: String,
    override val roles: List<String>,
    override val token: String,
    override val language: Language,
) : TokenBackedAuthInfo

data class BasicImpersonation(
    override val username: String
) : AuthInfo {
    override val roles: List<String>
        get() = emptyList()
    override val language: Language
        get() = Language.ENGLISH
}