package ch.awae.mycloud.auth.service

import ch.awae.mycloud.auth.ApiKeyUserAuthInfo
import ch.awae.mycloud.auth.AuthInfo
import ch.awae.mycloud.auth.AuthService
import ch.awae.mycloud.auth.BearerTokenUserAuthInfo
import ch.awae.mycloud.common.util.createLogger
import ch.awae.mycloud.auth.domain.AccountRepository
import ch.awae.mycloud.auth.domain.ApiKeyRepository
import ch.awae.mycloud.auth.domain.AuthoritiesMapper
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class AuthenticationService(
    private val accountRepository: AccountRepository,
    private val apiKeyRepository: ApiKeyRepository,
) : AuthService {

    private val log = createLogger()

    override fun authenticateToken(tokenString: String): AuthInfo? {
        return if (tokenString.startsWith("Bearer ")) {
            authenticateBearerToken(tokenString.substring(7))
        } else if (tokenString.startsWith("Key ")) {
            authenticateApiToken(tokenString.substring(4))
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
            BearerTokenUserAuthInfo(
                account.username,
                AuthoritiesMapper.getAuthorities(account),
                tokenString,
                account.language,
                account.email,
            )
        }
    }

    private fun authenticateApiToken(tokenString: String): ApiKeyUserAuthInfo? {
        return apiKeyRepository.findActiveByTokenString(tokenString)?.let { key ->
            ApiKeyUserAuthInfo(
                key.owner.username,
                AuthoritiesMapper.getAuthorities(key),
                key.tokenString,
                key.owner.language,
                key.owner.email,
                key.name,
            )
        }
    }

}