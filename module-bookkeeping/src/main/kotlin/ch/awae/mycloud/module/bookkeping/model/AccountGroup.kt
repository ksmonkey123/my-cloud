package ch.awae.mycloud.module.bookkeping.model

import ch.awae.mycloud.common.db.IdBaseEntity
import jakarta.persistence.*
import jakarta.validation.*
import org.springframework.data.jpa.repository.*

@Table(name = "account_group", schema = "bookkeeping")
@Entity(name = "BK_AccountGroup")
class AccountGroup(
    @ManyToOne @JoinColumn(updatable = false)
    val book: Book,
    @Column(updatable = false)
    val groupNumber: Int,
    var title: String,
    var locked: Boolean,
) : IdBaseEntity() {

    override fun validate() {
        if (groupNumber !in 1..9) {
            throw ValidationException("invalid group number")
        }
    }

    @OneToMany(mappedBy = "accountGroup", orphanRemoval = true)
    val accounts: List<Account> = emptyList()

}

interface AccountGroupRepository : JpaRepository<AccountGroup, Long> {

    fun getByBookAndGroupNumber(book: Book, groupNumber: Int): AccountGroup?

}
