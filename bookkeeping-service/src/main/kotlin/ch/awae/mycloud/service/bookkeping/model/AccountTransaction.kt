package ch.awae.mycloud.service.bookkeping.model

import jakarta.persistence.*
import jakarta.persistence.Table
import org.hibernate.annotations.*
import org.springframework.data.domain.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query
import java.math.*
import java.time.*

@Immutable
@Entity
@Table(name = "v_account_transaction")
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
    val creTime: LocalDateTime,
)

@Embeddable
class AccountTransactionPK(val recordId: Long, val accountId: Long)

interface AccountTransactionRepository : JpaRepository<AccountTransaction, AccountTransactionPK> {

    @Query("select t from AccountTransaction t where t.account = :account order by t.bookingDate desc, t.creTime desc")
    fun findByAccount(account: Account, pageable: Pageable): Page<AccountTransaction>

    @Query("select sum(t.amount) from AccountTransaction t where t.account = :account")
    fun getBalanceOfAccount(account: Account): BigDecimal?

}