package ch.awae.mycloud.service.bookkeping.model

import ch.awae.mycloud.db.*
import jakarta.persistence.*
import java.time.*

@Entity
class Book(
    @Column(updatable = false)
    val username: String,
    var title: String,
    @Column(updatable = false)
    val openingDate: LocalDate,
    var closingDate: LocalDate,
) : IdBaseEntity() {
    @OneToMany(mappedBy = "book")
    val accountGroups: List<AccountGroup> = emptyList()

    @OneToMany(mappedBy = "book")
    val accounts: List<Account> = emptyList()

}
