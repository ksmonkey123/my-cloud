package ch.awae.mycloud.service.auth.dto

import ch.awae.mycloud.service.auth.domain.*

data class AccountSummaryDto(
    val username: String,
    val enabled: Boolean,
    val admin: Boolean,
) {
    constructor(account: Account) : this(account.username, account.enabled, account.admin)
}
