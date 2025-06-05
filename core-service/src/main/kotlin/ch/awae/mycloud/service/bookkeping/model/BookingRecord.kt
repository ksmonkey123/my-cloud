package ch.awae.mycloud.service.bookkeping.model

import ch.awae.mycloud.db.*
import jakarta.persistence.*
import jakarta.validation.*
import org.springframework.data.domain.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query
import java.math.*
import java.time.*

@Table(name = "bookkeeping_booking_record")
@Entity
class BookingRecord(
    @ManyToOne @JoinColumn(updatable = false)
    val book: Book,
    val localId: Long,
    var bookingText: String,
    var description: String?,
    val bookingDate: LocalDate,
    var tag: String?,
) : IdBaseEntity() {

    @ElementCollection
    @CollectionTable(name = "bookkeeping_booking_movement", joinColumns = [JoinColumn(name = "booking_record_id")])
    @MapKeyJoinColumn(name = "account_id")
    @Column(name = "amount")
    val movements: MutableMap<BookAccount, BigDecimal> = mutableMapOf()

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

    @Query("select r from BookingRecord r where r.book = :book order by r.bookingDate desc, r._creationTimestamp desc")
    fun listAllInBook(book: Book, pageable: Pageable): Page<BookingRecord>

    fun findByLocalIdAndBook(id: Long, book: Book): BookingRecord?

    @Query("select r from BookingRecord r where r.book = :book order by r.bookingDate asc, r._creationTimestamp asc limit 1")
    fun findFirstInBook(book: Book): BookingRecord?

    @Query("select max(r.localId) from BookingRecord r where r.book = :book")
    fun findMaxLocalId(book: Book): Long?

}