package ch.awae.mycloud.service.bookkeping.model

import ch.awae.mycloud.db.*
import ch.awae.mycloud.service.bookkeping.dto.*
import ch.awae.mycloud.service.bookkeping.model.converter.*
import jakarta.persistence.*
import jakarta.validation.ValidationException
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query

@Entity
class Account(
    @Column(updatable = false)
    val accountNumber: Int,
    @ManyToOne @JoinColumn(updatable = false)
    val accountGroup: AccountGroup,
    @Column(updatable = false)
    @Convert(converter = AccountTypeConverter::class)
    val accountType: AccountType,
    var title: String,
    var description: String?,
) : IdBaseEntity() {

    override fun validate() {
        if (accountNumber !in 1..999) {
            throw ValidationException("Invalid account number")
        }
    }

}

interface AccountRepository : JpaRepository<Account, Long> {

    @Query("select a from Account a where a.accountGroup.book = :book order by a.accountGroup.groupNumber asc, a.accountNumber asc")
    fun listAllOfBook(book: Book): List<Account>

    @Query("select a from Account a where a.accountGroup = :group and a.accountNumber = :accountNumber")
    fun findByGroupAndAccountNumber(group: AccountGroup, accountNumber: Int): Account?

    @Query("select a from Account a where a.accountNumber = :accountNumber and a.accountGroup.groupNumber = :groupNumber and a.accountGroup.book = :book")
    fun findByIdentifier(book: Book, groupNumber: Int, accountNumber: Int): Account?

}