package ch.awae.mycloud.auth.service

import ch.awae.mycloud.auth.*
import ch.awae.mycloud.auth.audit.AuditLog
import ch.awae.mycloud.auth.domain.ApiKeyRepository
import ch.awae.mycloud.auth.domain.AuthTokenRepository
import ch.awae.mycloud.auth.domain.AuthoritiesMapper
import ch.awae.mycloud.common.util.createLogger
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class AuthenticationService(
    private val authTokenRepository: AuthTokenRepository,
    private val apiKeyRepository: ApiKeyRepository,
    private val auditLog: AuditLog,
) : AuthService {

    private val log = createLogger()

    override fun authenticateToken(tokenString: String, context: RequestContext?): AuthInfo? {
        return if (tokenString.startsWith("Bearer ")) {
            authenticateBearerToken(tokenString.substring(7), context)
        } else if (tokenString.startsWith("Key ")) {
            authenticateApiToken(tokenString.substring(4), context)
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

    private fun authenticateBearerToken(tokenString: String, context: RequestContext?): BearerTokenUserAuthInfo? {
        return authTokenRepository.findByValidTokenStringForActiveAccount(tokenString)?.let { token ->
            if (context != null) {
                auditLog.recordAccess(token.account.id, context, tokenId = token.id)
            }
            BearerTokenUserAuthInfo(
                token.account.username,
                AuthoritiesMapper.getAuthorities(token.account),
                token.tokenString,
                token.account.language,
                token.account.email,
            )
        }
    }

    private fun authenticateApiToken(tokenString: String, context: RequestContext?): ApiKeyUserAuthInfo? {
        return apiKeyRepository.findActiveByTokenString(tokenString)?.let { key ->
            if (context != null) {
                auditLog.recordAccess(key.owner.id, context, keyId = key.id)
            }
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