package ch.awae.mycloud.module.bookkeping.dto

import java.math.BigDecimal
import java.time.LocalDate

data class LedgerTransactionDto(
    val id: Long,
    val bookingDate: LocalDate,
    val tag: String?,
    val text: String,
    val description: String?,
    val amount: BigDecimal,
)
