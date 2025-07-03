package ch.awae.mycloud.module.auth.service

import ch.awae.mycloud.api.auth.*
import ch.awae.mycloud.module.auth.domain.*
import jakarta.transaction.*
import org.springframework.stereotype.*

@Service
@Transactional
class AuthenticationService(
    private val accountRepository: AccountRepository
) : AuthService {

    fun createUserAuthInfo(account: Account, tokenString: String): BearerTokenUserAuthInfo = BearerTokenUserAuthInfo(
        account.username,
        AccountToRoleMapper.getRoles(account, includeUserRole = true),
        tokenString,
        account.language,
        account.email,
    )

    override fun authenticateToken(tokenString: String): AuthInfo? {
        return if (tokenString.startsWith("Bearer ")) {
            authenticateBearerToken(tokenString.substring(7))
        } else {
            null
        }
    }

    private fun authenticateBearerToken(tokenString: String): BearerTokenUserAuthInfo? {
        return accountRepository.findActiveByValidTokenString(tokenString)?.let { account ->
            createUserAuthInfo(account, tokenString)
        }
    }

}