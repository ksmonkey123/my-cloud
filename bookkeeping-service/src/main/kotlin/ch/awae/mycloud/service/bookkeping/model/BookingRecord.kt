package ch.awae.mycloud.service.bookkeping.model

import ch.awae.mycloud.db.*
import jakarta.persistence.*
import jakarta.validation.ValidationException
import org.springframework.data.jpa.repository.JpaRepository
import java.math.*
import java.time.*

@Entity
class BookingRecord(
    @ManyToOne @JoinColumn(updatable = false)
    val book: Book,
    var bookingText: String,
    var description: String?,
    var bookingDate: LocalDate,
    var tag: String?,
) : IdBaseEntity() {

    @ElementCollection
    @CollectionTable(name = "booking_movement", joinColumns = [JoinColumn(name = "booking_record_id")])
    @MapKeyJoinColumn(name = "account_id")
    @Column(name = "amount")
    val movements: MutableMap<Account, BigDecimal> = mutableMapOf()

    override fun validate() {
        // booking date must be while book is open
        if (bookingDate.isBefore(book.openingDate) || bookingDate.isAfter(book.closingDate)) {
            throw ValidationException("a booking cannot lie outside the open period of a book!")
        }

        // no cross-book movements
        if (movements.any { (account, _) -> account.accountGroup.book.id != book.id }) {
            throw ValidationException("All accounts in a booking record must belong to the same book as the record itself")
        }

        // movements must be balanced
        val netBalance = movements.values.reduce(BigDecimal::plus)
        if (netBalance.compareTo(BigDecimal.ZERO) != 0) {
            throw ValidationException("movements in a booking record must balance out! balance is $netBalance")
        }

        // no movement can be negative
        if (movements.values.any { it.compareTo(BigDecimal.ZERO) == 0 }) {
            throw ValidationException("movement in a booking record cannot have an amount of zero")
        }
    }

}

interface BookingRecordRepository : JpaRepository<BookingRecord, Long> {

    fun existsByBook(book: Book): Boolean

}