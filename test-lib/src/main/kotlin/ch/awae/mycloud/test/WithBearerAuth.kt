package ch.awae.mycloud.test

import ch.awae.mycloud.api.auth.*
import org.springframework.security.core.context.*
import org.springframework.security.test.context.support.*
import java.lang.annotation.*

@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@WithSecurityContext(factory = WithBearerAuthSecurityContextactory::class)
annotation class WithBearerAuth(
    val username: String = "user",
    val admin: Boolean = false,
    val email: String = "",
    val authorities: Array<String> = [],
    val language: Language = Language.ENGLISH,
    val token: String = "dummy-token",
)

class WithBearerAuthSecurityContextactory : WithSecurityContextFactory<WithBearerAuth> {
    override fun createSecurityContext(annotation: WithBearerAuth): SecurityContext? {
        val auth = BearerTokenUserAuthInfo(
            annotation.username,
            setOfNotNull("user", if (annotation.admin) "admin" else null, *annotation.authorities),
            annotation.token,
            annotation.language,
            annotation.email.takeIf { it.isNotBlank() },
        ).toAuthentication()
        val context = SecurityContextHolder.getContextHolderStrategy().context
        context.authentication = auth
        return context
    }

}