package ch.awae.mycloud.module.bookkeping.dto

import ch.awae.mycloud.common.PageDto
import java.math.*
import java.time.*

data class AccountLedgerDto(
    val account: AccountSummaryDto,
    val transactions: PageDto<LedgerTransactionDto>,
    val finalBalance: BigDecimal,
)

data class LedgerTransactionDto(
    val id: Long,
    val bookingDate: LocalDate,
    val tag: String?,
    val text: String,
    val description: String?,
    val amount: BigDecimal,
)
