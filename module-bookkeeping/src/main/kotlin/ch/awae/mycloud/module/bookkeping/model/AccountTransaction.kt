package ch.awae.mycloud.module.bookkeping.model

import jakarta.persistence.*
import jakarta.persistence.Table
import org.hibernate.annotations.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query
import java.math.*
import java.time.*

@Immutable
@Entity(name = "BK_AccountTransaction")
@Table(name = "v_account_transaction", schema = "bookkeeping")
@IdClass(AccountTransactionPK::class)
class AccountTransaction(
    @Id
    val recordId: Long,
    @Id @Column(name = "account_id")
    val accountId: Long,
    val bookingDate: LocalDate,
    val bookingText: String,
    val description: String?,
    val tag: String?,
    val amount: BigDecimal,
    @ManyToOne
    val account: Account,
)

@Embeddable
data class AccountTransactionPK(val recordId: Long, val accountId: Long)

interface AccountTransactionRepository : JpaRepository<AccountTransaction, AccountTransactionPK> {

    @Query("select t from BK_AccountTransaction t where t.account = :account order by t.bookingDate asc, t.recordId asc")
    fun findByAccount(account: Account): List<AccountTransaction>

}