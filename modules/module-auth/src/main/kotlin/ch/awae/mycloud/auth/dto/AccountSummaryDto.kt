package ch.awae.mycloud.auth.dto

import ch.awae.mycloud.auth.domain.*

data class AccountSummaryDto(
    val username: String,
    val enabled: Boolean,
    val admin: Boolean,
    val email: String?,
) {
    constructor(account: Account) : this(account.username, account.enabled, account.admin, account.email)
}
