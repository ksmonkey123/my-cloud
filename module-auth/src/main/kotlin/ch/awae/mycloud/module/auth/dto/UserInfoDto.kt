package ch.awae.mycloud.module.auth.dto

import ch.awae.mycloud.auth.Language
import ch.awae.mycloud.auth.UserInfo

data class UserInfoDto(
    override val username: String,
    override val email: String?,
    override val language: Language,
    override val enabled: Boolean,
) : UserInfo