package ch.awae.mycloud.module.bookkeping.model

import ch.awae.mycloud.common.db.*
import jakarta.persistence.*
import jakarta.validation.*
import org.springframework.data.jpa.repository.*

@Table(name = "account_group", schema = "bookkeeping")
@Entity(name = "bookkeeping_AccountGroup")
class AccountGroup(
    @ManyToOne @JoinColumn(updatable = false, nullable = false)
    val book: Book,
    @Column(updatable = false, nullable = false)
    val groupNumber: Int,
    @Column(nullable = false)
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
