package ch.awae.mycloud.module.auth.service

import ch.awae.mycloud.api.auth.*
import ch.awae.mycloud.common.*
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
) {

    private val logger = createLogger()

    @Throws(BadLoginException::class)
    fun authenticateCredentials(username: String, password: String): Account {
        val account = accountRepository.findActiveByUsername(username)
            ?.takeIf { passwordEncoder.matches(password, it.password) }
            ?: throw BadLoginException()
        logger.info("authenticated account $username")
        return account
    }

    @Throws(BadLoginException::class)
    fun login(username: String, password: String, retentionPolicy: TokenRetentionPolicy): AuthToken {
        val account = authenticateCredentials(username, password)
        val token = AuthToken.buildToken(account, LocalDateTime.now().plus(retentionPolicy.duration))

        AuthInfo.impersonate(username) {
            return authTokenRepository.saveAndFlush(token)
        }
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
