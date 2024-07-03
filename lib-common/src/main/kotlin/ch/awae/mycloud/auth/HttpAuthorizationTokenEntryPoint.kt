package ch.awae.mycloud.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class HttpAuthorizationTokenEntryPoint : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        if (response.status != HttpServletResponse.SC_FORBIDDEN &&
            authException is AuthenticationCredentialsNotFoundException
        ) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
        }
    }

}
