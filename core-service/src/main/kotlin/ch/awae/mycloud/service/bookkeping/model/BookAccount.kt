package ch.awae.mycloud.service.bookkeping.model

import ch.awae.mycloud.common.db.IdBaseEntity
import ch.awae.mycloud.service.bookkeping.dto.*
import ch.awae.mycloud.service.bookkeping.model.converter.*
import jakarta.persistence.*
import jakarta.validation.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query

@Table(name = "bookkeeping_account")
@Entity
class BookAccount(
    @Column(updatable = false)
    val accountNumber: Int,
    @ManyToOne @JoinColumn(updatable = false)
    val accountGroup: AccountGroup,
    @Column(updatable = false)
    @Convert(converter = AccountTypeConverter::class)
    val accountType: AccountType,
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

interface BookAccountRepository : JpaRepository<BookAccount, Long> {

    @Query("select a from BookAccount a where a.accountGroup.book = :book order by a.accountGroup.groupNumber asc, a.accountNumber asc")
    fun listAllOfBook(book: Book): List<BookAccount>

    @Query("select a from BookAccount  a where a.accountGroup = :group and a.accountNumber = :accountNumber")
    fun findByGroupAndAccountNumber(group: AccountGroup, accountNumber: Int): BookAccount?

    @Query("select a from BookAccount  a where a.accountNumber = :accountNumber and a.accountGroup.groupNumber = :groupNumber and a.accountGroup.book = :book")
    fun findByIdentifier(book: Book, groupNumber: Int, accountNumber: Int): BookAccount?

}