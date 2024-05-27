package ch.awae.mycloud.service.bookkeping.model

import ch.awae.mycloud.db.*
import jakarta.persistence.*
import java.math.*
import java.time.*

@Entity
class BookingRecord(
    @ManyToOne @JoinColumn(updatable = false)
    val book: Book,
    var bookingText: String,
    var description: String?,
    var invoiceDate: LocalDate,
    var bookingDate: LocalDate,
) : IdBaseEntity() {

    @ElementCollection
    @CollectionTable(name = "booking_movement", joinColumns = [JoinColumn(name = "booking_record_id")])
    @MapKeyJoinColumn(name = "account_id")
    @Column(name = "amount")
    val movements: MutableMap<Account, BigDecimal> = mutableMapOf()

}
