package ch.awae.mycloud.service.auth.dto

import ch.awae.mycloud.service.auth.domain.*

data class AccountDetailsDto(
    val username: String,
    val enabled: Boolean,
    val admin: Boolean,
    val roles: List<String>,
) {
    constructor(account: Account) : this(
        account.username,
        account.enabled,
        account.admin,
        account.roles.map { it.name }
    )

}
