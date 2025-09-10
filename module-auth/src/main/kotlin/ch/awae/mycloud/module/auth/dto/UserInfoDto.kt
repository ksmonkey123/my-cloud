package ch.awae.mycloud.module.auth.dto

import ch.awae.mycloud.api.auth.*

data class UserInfoDto(
    override val username: String,
    override val email: String?,
    override val language: Language,
    override val enabled: Boolean,
) : UserInfo