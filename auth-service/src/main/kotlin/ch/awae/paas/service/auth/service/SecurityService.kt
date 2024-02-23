package ch.awae.paas.service.auth.service

import ch.awae.paas.*
import ch.awae.paas.audit.*
import ch.awae.paas.auth.*
import ch.awae.paas.service.auth.*
import ch.awae.paas.service.auth.domain.*
import ch.awae.paas.service.auth.dto.*
import ch.awae.paas.service.auth.exception.*
import jakarta.transaction.*
import org.springframework.security.crypto.password.*
import org.springframework.stereotype.*

@Service
@Transactional
class SecurityService(
    private val accountRepository: AccountRepository,
    private val authTokenRepository: AuthTokenRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    val logger = createLogger()

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
        return authTokenRepository.saveAndFlush(AuthToken.buildToken(account))
    }

    @AuditLog
    fun logout(@NoAudit token: String) {
        authTokenRepository.deleteByTokenString(token)
    }


}
