package ch.awae.mycloud.service.bookkeping.model

import ch.awae.mycloud.db.*
import jakarta.persistence.*
import java.math.*

@Entity
class Account(
    @ManyToOne @JoinColumn(updatable = false)
    val book: Book,
    @ManyToOne
    var accountGroup: AccountGroup?,
    @Column(updatable = false)
    val accountType: AccountType,
    var title: String,
    var initialBalance: BigDecimal,
) : IdBaseEntity() {

    @ElementCollection
    @CollectionTable(name = "booking_movement", joinColumns = [JoinColumn(name = "account_id")])
    @MapKeyJoinColumn(name = "booking_record_id")
    @Column(name = "amount")
    val movements: MutableMap<BookingRecord, BigDecimal> = mutableMapOf()

}
