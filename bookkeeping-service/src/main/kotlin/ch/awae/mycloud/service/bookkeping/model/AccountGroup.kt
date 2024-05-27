package ch.awae.mycloud.service.bookkeping.model

import ch.awae.mycloud.db.*
import jakarta.persistence.*

@Entity
class AccountGroup(
    @ManyToOne @JoinColumn(updatable = false)
    val book: Book,
    @ManyToOne @JoinColumn(updatable = false)
    val parent: AccountGroup?,
    var title: String,
) : IdBaseEntity() {
    @OneToMany(mappedBy = "accountGroup")
    val accounts: List<Account> = emptyList()

    @OneToMany(mappedBy = "parent")
    val children: List<AccountGroup> = emptyList()
}
