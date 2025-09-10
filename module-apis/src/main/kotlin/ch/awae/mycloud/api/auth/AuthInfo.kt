package ch.awae.mycloud.api.auth

import org.springframework.security.authentication.*
import org.springframework.security.core.*
import org.springframework.security.core.authority.*
import org.springframework.security.core.context.*

sealed interface AuthInfo {
    val username: String
    val authorities: Set<String>
    val language: Language
    val email: String?

    fun toAuthentication(): Authentication {
        return UsernamePasswordAuthenticationToken(
            this.username,
            this,
            this.authorities.map(::SimpleGrantedAuthority)
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
    }
}

data class BearerTokenUserAuthInfo(
    override val username: String,
    override val authorities: Set<String>,
    val token: String,
    override val language: Language,
    override val email: String?,
) : AuthInfo
