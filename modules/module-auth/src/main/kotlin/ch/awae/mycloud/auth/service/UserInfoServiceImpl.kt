package ch.awae.mycloud.auth.service

import ch.awae.mycloud.auth.UserInfo
import ch.awae.mycloud.auth.UserInfoService
import ch.awae.mycloud.auth.domain.*
import ch.awae.mycloud.auth.dto.*
import jakarta.transaction.*
import org.springframework.stereotype.*

@Service
@Transactional
class UserInfoServiceImpl(val accountRepository: AccountRepository) : UserInfoService {

    override fun getUserInfo(username: String): UserInfo? {
        return accountRepository.findByUsername(username)?.let { user ->
            UserInfoDto(
                username = user.username,
                email = user.email,
                language = user.language,
                enabled = user.enabled,
            )
        }
    }

}