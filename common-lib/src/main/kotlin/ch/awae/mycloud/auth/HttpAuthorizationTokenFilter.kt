package ch.awae.mycloud.auth

import jakarta.servlet.*
import jakarta.servlet.http.*
import org.springframework.security.core.context.*
import org.springframework.stereotype.*
import org.springframework.web.filter.*

@Component
class HttpAuthorizationTokenFilter(val authServiceClient: AuthService) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val auth = request.getHeader("Authorization")?.let(authServiceClient::authenticateToken)

        if (auth != null) {
            logger.info("authenticated user '${auth.username}' for ${request.method} ${request.requestURI}")
        } else if (!request.requestURI.startsWith("/actuator/")) {
            // do not log actuator endpoints
            logger.info("no authentication provided for ${request.method} ${request.requestURI}")
        }

        SecurityContextHolder.getContext().authentication = auth?.toAuthentication()

        filterChain.doFilter(request, response)
    }

}
