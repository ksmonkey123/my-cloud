package ch.awae.mycloud.bookkeping.dto

import ch.awae.mycloud.bookkeping.model.*

data class AccountGroupDto(
    val groupNumber: Int,
    val title: String,
    val locked: Boolean,
    val accounts: List<AccountSummaryDto>,
) {

    constructor(accountGroup: AccountGroup) : this(
        groupNumber = accountGroup.groupNumber,
        title = accountGroup.title,
        locked = accountGroup.locked,
        accountGroup.accounts.map { AccountSummaryDto(it) }.sortedBy { it.id },
    )
}

