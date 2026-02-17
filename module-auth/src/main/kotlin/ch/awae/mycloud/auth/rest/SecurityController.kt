package ch.awae.mycloud.auth.rest

import ch.awae.mycloud.auth.AuthInfo
import ch.awae.mycloud.auth.BearerTokenUserAuthInfo
import ch.awae.mycloud.common.util.createLogger
import ch.awae.mycloud.auth.domain.TokenRetentionPolicy
import ch.awae.mycloud.auth.dto.AuthInfoDto
import ch.awae.mycloud.auth.service.SecurityService
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
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

        val policy = when (request.longRetention) {
            false -> TokenRetentionPolicy.SHORT
            true -> TokenRetentionPolicy.LONG
        }

        val authToken = securityService.login(request.username, request.password, policy)
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
    @PreAuthorize("hasAnyAuthority('user', 'api')")
    fun getOwnAccountInfo(): AuthInfoDto = AuthInfoDto.of(AuthInfo.info)

    data class LoginRequest(val username: String, val password: String, val longRetention: Boolean)
    data class LoginResponse(val token: String)

}
