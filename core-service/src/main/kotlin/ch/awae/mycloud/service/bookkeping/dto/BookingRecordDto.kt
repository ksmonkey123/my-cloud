package ch.awae.mycloud.service.bookkeping.dto

import ch.awae.mycloud.service.bookkeping.model.*
import java.math.*
import java.time.*

data class BookingRecordDto(
    val id: Long,
    val bookingDate: LocalDate,
    val tag: String?,
    val text: String,
    val description: String?,
    val amount: BigDecimal,
    val credits: List<BookingRecordMovementDto>,
    val debits: List<BookingRecordMovementDto>,
) {

    companion object {
        fun of(bookingRecord: BookingRecord): BookingRecordDto {
            val credits = bookingRecord.movements.filterValues { it > BigDecimal.ZERO }
                .map { (account, amount) -> BookingRecordMovementDto(AccountId.of(account), amount) }
            val debits = bookingRecord.movements.filterValues { it < BigDecimal.ZERO }
                .map { (account, amount) -> BookingRecordMovementDto(AccountId.of(account), -amount) }

            return BookingRecordDto(
                id = bookingRecord.localId,
                bookingDate = bookingRecord.bookingDate,
                tag = bookingRecord.tag,
                text = bookingRecord.bookingText,
                description = bookingRecord.description,
                amount = credits.sumOf { it.amount },
                credits = credits,
                debits = debits,
            )
        }
    }
}

data class CreateBookingRecordRequest(
    val bookingDate: LocalDate,
    val tag: String?,
    val text: String,
    val description: String?,
    val credits: List<BookingRecordMovementDto>,
    val debits: List<BookingRecordMovementDto>,
)

data class BookingRecordMovementDto(
    val accountId: String,
    val amount: BigDecimal,
) {
    constructor(accountId: AccountId, amount: BigDecimal) : this(accountId.toString(), amount)
}

data class BookingRecordEditRequest(
    val text: String,
    val tag: String?,
    val description: String?,
)