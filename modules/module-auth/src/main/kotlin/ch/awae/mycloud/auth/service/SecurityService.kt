package ch.awae.mycloud.auth.service

import ch.awae.mycloud.common.TokenGenerator
import ch.awae.mycloud.common.util.createLogger
import ch.awae.mycloud.auth.domain.*
import ch.awae.mycloud.auth.exception.BadLoginException
import jakarta.transaction.*
import net.javacrumbs.shedlock.spring.annotation.*
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
    private val tokenGenerator: TokenGenerator,
) {

    private val logger = createLogger()

    @Throws(BadLoginException::class)
    fun login(username: String, password: String, retentionPolicy: TokenRetentionPolicy): AuthToken {
        val account = accountRepository.findActiveByUsername(username)
            ?.takeIf { passwordEncoder.matches(password, it.password) }
            ?: throw BadLoginException()

        logger.info("authenticated account $username")
        val token = AuthToken.buildToken(account, tokenGenerator, LocalDateTime.now().plus(retentionPolicy.duration))

        return authTokenRepository.saveAndFlush(token)
    }

    fun logout(token: String) {
        authTokenRepository.deleteByTokenString(token)
    }

    @SchedulerLock(name = "auth:expired-token-cleaner")
    @Scheduled(cron = "\${auth.clean-timer.schedule}")
    fun cleanOldTokens() {
        authTokenRepository.deleteExpiredTokens()
    }

}
