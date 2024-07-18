package ch.awae.mycloud.service.bookkeping.dto

import ch.awae.mycloud.service.bookkeping.model.*

data class AccountGroupDto(
    val groupNumber: Int,
    val title: String,
    val accounts: List<AccountSummaryDto>,
) {

    constructor(accountGroup: AccountGroup) : this(
        groupNumber = accountGroup.groupNumber,
        title = accountGroup.title,
        accountGroup.accounts.map { AccountSummaryDto(it) }.sortedBy { it.id },
    )
}

