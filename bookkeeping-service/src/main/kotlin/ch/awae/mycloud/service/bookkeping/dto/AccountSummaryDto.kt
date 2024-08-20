package ch.awae.mycloud.service.bookkeping.dto

import ch.awae.mycloud.service.bookkeping.model.*
import java.math.*

data class AccountSummaryDto(
    val id: String,
    val title: String,
    val description: String?,
    val accountType: AccountType,
    val balance: BigDecimal,
) {
    constructor(account: Account) : this(
        id = AccountId.of(account).toString(),
        title = account.title,
        description = account.description,
        accountType = account.accountType,
        balance = account.balance?.balance ?: BigDecimal.ZERO,
    )
}