package ch.awae.mycloud.module.auth.rest

import ch.awae.mycloud.api.auth.*
import ch.awae.mycloud.common.*
import ch.awae.mycloud.module.auth.dto.AuthInfoDto
import ch.awae.mycloud.module.auth.service.*
import org.springframework.http.*
import org.springframework.security.access.prepost.*
import org.springframework.web.bind.annotation.*

/**
 * Security relevant endpoints:
 *  - login
 *  - logout
 */
@RestController
@RequestMapping("/rest/auth")
class SecurityController(private val securityService: SecurityService) {

    private val logger = createLogger()

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): LoginResponse {
        logger.info("handling login request for ${request.username}")
        val authToken = securityService.login(request.username, request.password)
        return LoginResponse(authToken.tokenString)
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logout() {
        val auth = AuthInfo.info as BearerTokenUserAuthInfo

        logger.info("handling logout request for ${auth.username}")
        securityService.logout(auth.token)
    }

    @GetMapping("/authenticate")
    @PreAuthorize("hasAuthority('user')")
    fun getOwnAccountInfo(): AuthInfoDto = AuthInfoDto.of(AuthInfo.info!!)

    data class LoginRequest(val username: String, val password: String)
    data class LoginResponse(val token: String)

}
