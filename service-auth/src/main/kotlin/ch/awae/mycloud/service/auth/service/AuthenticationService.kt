package ch.awae.mycloud.service.auth.service

import ch.awae.mycloud.auth.*
import ch.awae.mycloud.service.auth.domain.*
import jakarta.transaction.*
import org.springframework.stereotype.*

@Service
@Transactional
class AuthenticationService(
    private val accountRepository: AccountRepository
) : AuthService {

    fun createUserAuthInfo(account: Account, tokenString: String): UserAuthInfo = UserAuthInfo(
        account.username,
        account.roles
            .filter { it.enabled }
            .map { it.name }
            .let { roles ->
                if (account.admin) {
                    roles + arrayOf("admin", "user")
                } else {
                    roles + "user"
                }
            },
        tokenString
    )

    override fun authenticateToken(tokenString: String): AuthInfo? {
        return if (tokenString.startsWith("Bearer ")) {
            authenticateBearerToken(tokenString.substring(7))
        } else {
            null
        }
    }

    private fun authenticateBearerToken(tokenString: String): UserAuthInfo? {
        return accountRepository.findActiveByTokenString(tokenString)?.let { account ->
            createUserAuthInfo(account, tokenString)
        }
    }

}