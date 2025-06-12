package ch.awae.mycloud.module.auth.service

import ch.awae.mycloud.api.auth.*
import ch.awae.mycloud.module.auth.domain.*
import ch.awae.mycloud.module.auth.dto.*
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
                roles = AccountToRoleMapper.getRoles(user),
            )
        }
    }

}