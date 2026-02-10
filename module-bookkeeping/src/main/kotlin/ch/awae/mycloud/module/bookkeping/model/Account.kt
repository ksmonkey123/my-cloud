package ch.awae.mycloud.module.bookkeping.model

import ch.awae.mycloud.common.db.*
import ch.awae.mycloud.module.bookkeping.dto.*
import ch.awae.mycloud.module.bookkeping.model.converter.*
import jakarta.persistence.*
import jakarta.validation.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.*

@Table(name = "account", schema = "bookkeeping")
@Entity(name = "bookkeeping_Account")
class Account(
    @Column(updatable = false, nullable = false)
    val accountNumber: Int,
    @ManyToOne @JoinColumn(updatable = false, nullable = false)
    val accountGroup: AccountGroup,
    @Column(updatable = false, nullable = false)
    @Convert(converter = AccountTypeConverter::class)
    val accountType: AccountType,
    @Column(nullable = false)
    var title: String,
    var description: String?,
    var locked: Boolean,
) : IdBaseEntity() {

    @OneToOne(mappedBy = "account")
    val balance: AccountBalance? = null

    override fun validate() {
        if (accountNumber !in 1..999) {
            throw ValidationException("Invalid account number")
        }
    }

    fun toShortString() = AccountId.of(this).toString() + " (${accountType.shortString})"

}

interface AccountRepository : JpaRepository<Account, Long> {

    @Query("select a from bookkeeping_Account a where a.accountGroup.book = :book order by a.accountGroup.groupNumber asc, a.accountNumber asc")
    fun listAllOfBook(book: Book): List<Account>

    @Query("select a from bookkeeping_Account  a where a.accountGroup = :group and a.accountNumber = :accountNumber")
    fun findByGroupAndAccountNumber(group: AccountGroup, accountNumber: Int): Account?

    @Query("select a from bookkeeping_Account  a where a.accountNumber = :accountNumber and a.accountGroup.groupNumber = :groupNumber and a.accountGroup.book = :book")
    fun findByIdentifier(book: Book, groupNumber: Int, accountNumber: Int): Account?

}