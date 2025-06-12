package ch.awae.mycloud.module.auth.dto

import ch.awae.mycloud.module.auth.domain.*

data class AccountDetailsDto(
    val username: String,
    val email: String?,
    val enabled: Boolean,
    val admin: Boolean,
    val roles: List<String>,
    val languageCode: String,
) {
    constructor(account: Account) : this(
        account.username,
        account.email,
        account.enabled,
        account.admin,
        account.roles.map { it.name },
        account.language.code,
    )

}
