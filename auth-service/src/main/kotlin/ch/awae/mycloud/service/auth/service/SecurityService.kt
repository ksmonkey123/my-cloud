package ch.awae.mycloud.service.auth.service

import ch.awae.mycloud.*
import ch.awae.mycloud.audit.*
import ch.awae.mycloud.auth.*
import ch.awae.mycloud.service.auth.*
import ch.awae.mycloud.service.auth.domain.*
import ch.awae.mycloud.service.auth.dto.*
import ch.awae.mycloud.service.auth.exception.*
import jakarta.transaction.*
import net.javacrumbs.shedlock.spring.annotation.*
import org.springframework.beans.factory.annotation.*
import org.springframework.scheduling.annotation.*
import org.springframework.security.crypto.password.*
import org.springframework.stereotype.*
import java.time.*

@Service
@Transactional
class SecurityService(
    private val authenticationService: AuthenticationService,
    private val accountRepository: AccountRepository,
    private val authTokenRepository: AuthTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    @Value("\${auth.clean-timer.max-age}")
    maxAge: String,
) {

    private val logger = createLogger()
    private val maxAge = Duration.parse(maxAge).abs()

    @Throws(BadLoginException::class)
    fun authenticateCredentials(username: String, password: String): Account {
        val account = accountRepository.findActiveByUsername(username)
            ?.takeIf { passwordEncoder.matches(password, it.password) }
            ?: throw BadLoginException()
        logger.info("authenticated account $username")
        return account
    }

    @AuditLog
    @Throws(BadLoginException::class)
    fun login(username: String, @NoAudit password: String): AuthToken {
        val account = authenticateCredentials(username, password)
        val token = AuthToken.buildToken(account)

        AuthInfo.impersonate(username) {
            return authTokenRepository.saveAndFlush(token)
        }
    }

    @AuditLog
    fun logout(@NoAudit token: String) {
        authTokenRepository.deleteByTokenString(token)
    }

    @SchedulerLock(name = "scan-timer")
    @Scheduled(cron = "\${auth.clean-timer.schedule}")
    fun cleanOldTokens() {
        authTokenRepository.deleteExpiredTokens(LocalDateTime.now().minus(maxAge))
    }

}
