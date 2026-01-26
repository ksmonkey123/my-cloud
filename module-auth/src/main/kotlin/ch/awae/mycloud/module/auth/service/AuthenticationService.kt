package ch.awae.mycloud.module.auth.service

import ch.awae.mycloud.api.auth.AuthInfo
import ch.awae.mycloud.api.auth.AuthService
import ch.awae.mycloud.api.auth.BearerTokenUserAuthInfo
import ch.awae.mycloud.common.createLogger
import ch.awae.mycloud.module.auth.domain.Account
import ch.awae.mycloud.module.auth.domain.AccountRepository
import ch.awae.mycloud.module.auth.domain.AccountToAuthoritiesMapper
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class AuthenticationService(
    private val accountRepository: AccountRepository
) : AuthService {

    private val log = createLogger()

    fun createUserAuthInfo(account: Account, tokenString: String): BearerTokenUserAuthInfo = BearerTokenUserAuthInfo(
        account.username,
        AccountToAuthoritiesMapper.getAuthorities(account),
        tokenString,
        account.language,
        account.email,
    )

    override fun authenticateToken(tokenString: String): AuthInfo? {
        return if (tokenString.startsWith("Bearer ")) {
            authenticateBearerToken(tokenString.substring(7))
        } else {
            val firstSpace = tokenString.indexOf(' ')
            if (firstSpace == -1) {
                log.warn("unsupported authentication scheme: no scheme found")
            } else {
                val scheme = tokenString.substring(0, firstSpace)
                log.warn("unsupported authentication scheme: {}", scheme)
            }
            null
        }
    }

    private fun authenticateBearerToken(tokenString: String): BearerTokenUserAuthInfo? {
        return accountRepository.findActiveByValidTokenString(tokenString)?.let { account ->
            createUserAuthInfo(account, tokenString)
        }
    }

}