package ch.awae.mycloud.test

import ch.awae.mycloud.auth.api.ApiKeyUserAuthInfo
import ch.awae.mycloud.auth.api.Language
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
@WithSecurityContext(factory = WithApiAuthSecurityContextactory::class)
@MustBeDocumented
annotation class WithApiAuth(
    val username: String = "user",
    val email: String = "",
    val authorities: Array<String> = [],
    val language: Language = Language.ENGLISH,
    val token: String = "dummy-token",
    val keyName: String = "dummy-key",
    @get:AliasFor(annotation = WithSecurityContext::class)
    val setupBefore: TestExecutionEvent = TestExecutionEvent.TEST_METHOD,
)

class WithApiAuthSecurityContextactory : WithSecurityContextFactory<WithApiAuth> {

    private var _securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy()

    override fun createSecurityContext(annotation: WithApiAuth): SecurityContext {
        val auth = ApiKeyUserAuthInfo(
            annotation.username,
            setOfNotNull("api", *annotation.authorities),
            annotation.token,
            annotation.language,
            annotation.email.takeIf { it.isNotBlank() },
            annotation.keyName
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