package ch.awae.mycloud.test

import ch.awae.mycloud.api.auth.BearerTokenUserAuthInfo
import ch.awae.mycloud.api.auth.Language
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.AliasFor
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolderStrategy
import org.springframework.security.test.context.support.TestExecutionEvent
import org.springframework.security.test.context.support.WithSecurityContext
import org.springframework.security.test.context.support.WithSecurityContextFactory
import java.lang.annotation.Inherited

@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@WithSecurityContext(factory = WithBearerAuthSecurityContextactory::class)
@MustBeDocumented
annotation class WithBearerAuth(
    val username: String = "user",
    val admin: Boolean = false,
    val email: String = "",
    val authorities: Array<String> = [],
    val language: Language = Language.ENGLISH,
    val token: String = "dummy-token",
    @get:AliasFor(annotation = WithSecurityContext::class)
    val setupBefore: TestExecutionEvent = TestExecutionEvent.TEST_METHOD,
)

class WithBearerAuthSecurityContextactory : WithSecurityContextFactory<WithBearerAuth> {

    private var _securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy()

    override fun createSecurityContext(annotation: WithBearerAuth): SecurityContext {
        val auth = BearerTokenUserAuthInfo(
            annotation.username,
            setOfNotNull("user", if (annotation.admin) "admin" else null, *annotation.authorities),
            annotation.token,
            annotation.language,
            annotation.email.takeIf { it.isNotBlank() },
        ).toAuthentication()
        val context = _securityContextHolderStrategy.createEmptyContext()
        context.authentication = auth
        return context
    }

    @Autowired(required = false)
    fun setSecurityContextHolderStrategy(holderStrategy: SecurityContextHolderStrategy) {
        _securityContextHolderStrategy = holderStrategy
    }

}