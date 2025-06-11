package ch.awae.mycloud.module.auth.service

import ch.awae.mycloud.api.auth.AuthInfo
import ch.awae.mycloud.common.createLogger
import ch.awae.mycloud.module.auth.domain.*
import ch.awae.mycloud.module.auth.exception.*
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

    @Throws(BadLoginException::class)
    fun login(username: String, password: String): AuthToken {
        val account = authenticateCredentials(username, password)
        val token = AuthToken.buildToken(account)

        AuthInfo.impersonate(username) {
            return authTokenRepository.saveAndFlush(token)
        }
    }

    fun logout(token: String) {
        authTokenRepository.deleteByTokenString(token)
    }

    @SchedulerLock(name = "auth-token-clean-timer")
    @Scheduled(cron = "\${auth.clean-timer.schedule}")
    fun cleanOldTokens() {
        authTokenRepository.deleteExpiredTokens(LocalDateTime.now().minus(maxAge))
    }

}
