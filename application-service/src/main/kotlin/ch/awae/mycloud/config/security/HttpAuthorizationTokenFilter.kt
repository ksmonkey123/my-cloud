package ch.awae.mycloud.config.security

import ch.awae.mycloud.api.auth.ApiKeyUserAuthInfo
import ch.awae.mycloud.api.auth.AuthService
import ch.awae.mycloud.api.auth.BearerTokenUserAuthInfo
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class HttpAuthorizationTokenFilter(val authServiceClient: AuthService) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val auth = request.getHeader("Authorization")?.trim()?.let(authServiceClient::authenticateToken)

        if (auth != null) {
            when (auth) {
                is BearerTokenUserAuthInfo -> logger.info("authenticated user '${auth.username}' for ${request.method} ${request.requestURI}")
                is ApiKeyUserAuthInfo -> logger.info("authenticated user '${auth.username}' by api key ${auth.keyName} for ${request.method} ${request.requestURI}")
            }
        } else if (!request.requestURI.startsWith("/actuator/")) {
            // do not log actuator endpoints
            logger.info("no authentication provided for ${request.method} ${request.requestURI}")
        }

        SecurityContextHolder.getContext().authentication = auth?.toAuthentication()

        filterChain.doFilter(request, response)
    }

}
